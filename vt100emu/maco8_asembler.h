#pragma once
#include <set>
#include <unordered_set>
#include <unordered_map>
#include <string>
#include <memory>
#include <array>

void debug_out(const char* fmt, ...);

namespace macro8 {
	enum class SymbolType {
		Undefined = 0,
		Keyword,
		Opcode,
		Mri,		//  & 177
		Macro,
	};
	struct Position {
		Position(uint32_t line, uint32_t col,uint32_t absolute) : col(col), line(line), absolute(absolute){}
		Position() : col(0), line(1), absolute(0) {}
		operator uint32_t() const { return uint32_t(line << 16 | col); }
		Position& operator=(uint32_t value) { line = value >> 16; col = value & 0xFFFF; return *this; }
		uint16_t col;
		uint16_t line;
		uint32_t absolute;
	};
	inline bool operator==(Position l, Position r) { return l.col == r.col && l.line == r.line; }
	inline bool operator!=(Position l, Position r) { return !(l == r); }
	inline bool operator<(Position l, Position r) { return  l.line < r.line && l.col < r.col; }
	class SymbolTable;
	typedef std::vector<std::unique_ptr<std::string>> StringTable;
	class Name {
	public:
		operator const std::string&() const { return *m_handle_lookup.at(m_handle); }
		size_t handle() const { return m_handle; }
	public:
		Name(size_t handle, const StringTable& handle_lookup) : m_handle(handle), m_handle_lookup(handle_lookup) {}
		friend class SymbolTable;
		size_t m_handle;
		const StringTable& m_handle_lookup;
	};

	inline bool operator==(const Name& l, const Name& r) { return l.handle() == r.handle(); }
	inline bool operator!=(const Name& l, const Name& r) { return !(l == r); }

	class Symbol {
		Symbol(const Name& name) : m_name(name), m_fixed(false), m_value(0), m_type(SymbolType::Undefined) {}
		void value(int value, int lineno) {
			m_lineno_defined.emplace(std::make_pair(value, lineno));
			m_value = value;
		}
		void fixed(bool value) { m_fixed = value; }
	public:
		const SymbolType type() const { return m_type; }
		const std::string& name() const { return m_name; }
		size_t name_handle() const { return m_name.handle(); }
		const bool fixed() const { return m_fixed; }
		
		int value() const { return m_value; }
		const int value(int lineno = -1) {
			if (lineno > 0) m_lineno_ref.emplace(lineno);
			return m_value;
		}
	private:
		friend class SymbolTable;
		Name m_name;
		std::set<int> m_lineno_ref;
		std::set<std::pair<int,int>> m_lineno_defined;
		SymbolType m_type;
		bool m_fixed;
		int m_value;
	};
	inline bool operator==(const Symbol& l, const Symbol& r) { return l.name_handle() == r.name_handle(); }
	inline bool operator!=(const Symbol& l, const Symbol& r) { return !(l == r); }

};


namespace std {
	template <>
	struct hash<macro8::Position> {
		std::size_t operator()(const macro8::Position& p) const { return size_t(p); }
	};
	template <>
	struct hash<macro8::Name> {
		std::size_t operator()(const macro8::Name& p) const { return std::hash<std::string>()(p); }
	};
	template <>
	struct hash<macro8::Symbol> {
		std::size_t operator()(const macro8::Symbol& p) const { return p.name_handle(); }
	};
};

namespace macro8 {
	class SymbolTableException : public std::exception {
		std::string m_what;
	public:
		SymbolTableException(const std::string& msg) : m_what(msg), std::exception() {}
		const char* what() const override { return m_what.c_str(); }
	};
	// need to make 
	class SymbolTable {
	public:
	
		SymbolTable(bool case_ignore = false) : m_case_ignore(false) , m_names(new StringTable) {}

		const Name& create_name(const std::string& name) {
			auto& h_name = m_names->emplace(m_names->end());
			h_name->reset(const_cast<std::string*>(&name)); // hack for lookup
			auto id = m_names_set.emplace(m_names->size() - 1 , *m_names.get());
			h_name->release(); // remeber, hack!
			if (id.second)
				h_name->reset(new std::string(name));
			else
				m_names->pop_back();
			return *id.first;
		}
		Symbol& lookup(const Name& name) { 
			auto it = m_table.find(name.handle());
			if (it == m_table.end()) {
				return m_table.emplace(std::make_pair(name.handle(), Symbol(name))).first->second;
			}
			return it->second;
		}
		Symbol& lookup(const std::string& name) { return lookup(create_name(name)); }
		Symbol& define(const std::string& name, int value, int lineno=-1,bool mri=false) {
			auto sym = lookup(name);
			if (sym.type() == SymbolType::Undefined) {
				sym.value(value, lineno);
				sym.m_type = mri ? SymbolType::Mri : SymbolType::Opcode;
				return sym;
			}
			throw SymbolTableException("Name '"+ name + "' already defined!");
		}
	private:
		friend class Symbol;
		friend class Name;
		
		std::unordered_map<size_t, Symbol> m_table;
		std::unordered_set<Name> m_names_set;

		std::unique_ptr<StringTable> m_names;
		bool m_case_ignore;
	};
	class Token {
	public:
		size_t size() const { return m_size; }
		int token() const { return m_token; }
		size_t pos() const { return m_pos; }
	private:
		Token() : m_token(-1),  m_size(0) , m_pos(0) {}
		friend class Lexer;
		int m_token;
		size_t m_size;
		size_t m_pos;
	};
	
	class Lexer {

	public:

		enum Kind {
			WHITESPACE = -6,
			COMMENT = -5,
			SYMBOL = -4,
			NUMBER = -3,
			INVALID = -2,
			ENDOFFILE = -1,
		};
		Lexer() {
		}
		template<typename T>
		static inline bool isLineEnding(T v) { return v == '\n' || v == '\r'; }
		bool m_skip_whitespace = false;
		// the tokenizer is set up so that break advances to the next char and continue restarts using the existing one
		void parse(const char* text, size_t size) {
			assert(text);
			Token t;
			t.m_token = 0;
			size_t pos = 0;
			while(true) {
				int ch = pos < size ? (uint8_t)text[pos] : 0;
				switch (t.m_token) {
				case 0:
					if (ch == 0) return; // all done
					t.m_pos = pos; // whatever it is, its a token
					t.m_size = 0; 
					if (::isspace(ch)) t.m_token = WHITESPACE; // skip spaces
					else if(::isalpha(ch))  t.m_token = SYMBOL; 
					else if(::isdigit(ch)) t.m_token = NUMBER; 
					else if (ch == '/') t.m_token = COMMENT;
					else {
						t.m_token = ch;
						t.m_size = 1;
						m_tokens.push_back(t); // single token
						t.m_token = 0; // clear
					}
					break; 
				case WHITESPACE:
					if (!::isspace(ch)) {
						if(!m_skip_whitespace) m_tokens.push_back(t);
						t.m_token = 0; // restart state
						continue;
					}
					break;
				case COMMENT: // comment
					if (isLineEnding(ch) || ch == 0) {
						m_tokens.push_back(t);
						t.m_token = 0; // restart state
						continue;
					}
					break;
				case SYMBOL:
					if (!::isalnum(ch)) {
						m_tokens.push_back(t);
						t.m_token = 0; // restart state
						continue;
					}
					break;
				case NUMBER:
					if (!::isdigit(ch)) {
						m_tokens.push_back(t);
						t.m_token = 0; // restart state
						continue;
					}
					break;
				default:
					assert(false);
					break; // error
				}
				pos++; // next charater
				t.m_size++; // save charater
			} 
		}
		void debug(const char* text) {
			std::string s;
			for (auto& t : m_tokens) {
				debug_out("pos: %3i size %4i ", t.pos(),t.size());
				switch (t.token()) {
				case COMMENT:
					s.assign(text + t.pos(), t.size());
					debug_out("COMMENT : %s\n", s.c_str());
					break;
				case SYMBOL:
					s.assign(text + t.pos(), t.size());
					debug_out("SYMBOL : %s\n", s.c_str());
					break;
				case NUMBER:
					s.assign(text + t.pos(), t.size());
					debug_out("NUMBER : %s\n", s.c_str());
					break;
				case WHITESPACE:
					debug_out("WHITESPACE\n");
					break;
				default:
					debug_out("TOK : 0x%2.2X", t.token());
					if(::isprint(t.token()))
						debug_out("('%c')", (char)t.token());
					debug_out("\n");
					break;
				}
			}
			
		}
		const std::vector<Token>& tokens() const { return m_tokens; }
	private:
		std::vector<Token> m_tokens;
	};

	
};