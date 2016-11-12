#pragma once
#include <set>
#include <unordered_set>
#include <unordered_map>
#include <string>
#include <memory>
#include <array>
#include <stack>

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

	// strings that self interns a string and is just a handle to it
	// 
	class istring {
		struct _istring {
			_istring() = delete;
			_istring(const _istring&) = delete;
			_istring(_istring&&) = delete;
			_istring* next;
			size_t size;
			size_t hash;
			char str[1];
		};
		const _istring* m_str;
		friend class istring_table;
	public:
		// removes all strings not being used
		static void collect_garbage();
		void clear();
		void assign(const char * str, size_t s);
		void assign(const istring& s);
		void assign(const char * str) { assign(str, ::strlen(str)); }
		void assign(const std::string& str) { assign(s.c_str(), s.size()); }
		void swap(istring&& s);
		// have to rull of 5 this garbage collection class
		istring() : m_str(nullptr) {}
		istring(const istring& s) : m_str(nullptr) { assign(s); }
		istring(istring&& s) : m_str(nullptr) { assign(s); }
		istring(const char* str) : m_str(nullptr) { assign(str); }
		istring(const char* str, size_t len) : m_str(nullptr) { assign(str,len); }
		istring& operator=(const istring& s)  { assign(s); return *this;  }
		istring& operator=(istring&& s)  { assign(s); s.clear(); return *this; }
		~istring() { clear(); }
		
		// some converters
		istring(const std::string& str) : m_str(nullptr) {  assign(str);  }
		istring(std::string&& str) : m_str(nullptr) {  assign(str); }
		operator std::string() { return m_str ? std::move(std::string(m_str->str, m_str->size)) : ""; }
		istring& operator=(const std::string& s) { assign(s); return *this; }
		istring& operator=(std::string&& s)  { assign(s); s.clear(); return *this; }
		bool equal(const istring& o) const { return m_str == o.m_str; }
		bool equal(const char* o) const { return m_str && m_str->str == o || ::strcmp(m_str->str, o) == 0; }
		// case invarent equal
		bool iequal(const istring& o) const { return (m_str && o.m_str &&  ::strcmpi(m_str->str, o.m_str->str)); }
		bool iequal(const char* o) const { return (m_str &&  ::strcmpi(m_str->str, o)); }
		
		const char* begin() const { return m_str ? m_str->str : nullptr; }
		const char* end() const { return m_str ? m_str->str + m_str->size: nullptr;}
		std::string::size_type size() const { return m_str ? m_str->size : 0; }
		std::string::value_type at(std::string::size_type i) const { return m_str ? m_str->str[i] : 0; }
		std::string::value_type operator[](std::string::size_type i) const { return m_str ? m_str->str[i] : 0; }

		size_t hash() const { return m_str ? m_str->hash : 0; }
		friend inline bool operator==(const istring& l, const istring& r) { return l.equal(r); }
		friend inline bool operator!=(const istring& l, const istring& r) { return !l.equal(r); }
	};
	
	
	class Symbol {
		Symbol(const std::string& name) : m_name(name), m_fixed(false), m_value(0), m_type(SymbolType::Undefined) {}
		void value(int value, int lineno) {
			m_lineno_defined.emplace(std::make_pair(value, lineno));
			m_value = value;
		}
		void fixed(bool value) { m_fixed = value; }
	public:
		const SymbolType type() const { return m_type; }
		const istring& name() const { return m_name; }
		const bool fixed() const { return m_fixed; }
		
		int value() const { return m_value; }
		const int value(int lineno = -1) {
			if (lineno > 0) m_lineno_ref.emplace(lineno);
			return m_value;
		}
	private:
		friend class SymbolTable;
		istring m_name;
		std::set<int> m_lineno_ref;
		std::set<std::pair<int,int>> m_lineno_defined;
		SymbolType m_type;
		bool m_fixed;
		int m_value;
		friend inline bool operator==(const Symbol& l, const Symbol& r) { return l == r || ::strcmpi( }
		friend inline bool operator!=(const Symbol& l, const Symbol& r) { return !(l == r); }
	};
	

};


namespace std {
	template <>
	struct hash<macro8::Position> {
		std::size_t operator()(const macro8::Position& p) const { return size_t(p); }
	};
	template <>
	struct hash<macro8::Symbol> {
		std::size_t operator()(const macro8::Symbol& p) const { return std::hash<std::string>()(p.name()); }
	};
	template <>
	struct hash<macro8::istring> {
		std::size_t operator()(const macro8::istring& p) const { return std::hash<std::string>()(p); }
	};
};

namespace macro8 {
	
	class AssemblerException : public std::exception {
		std::string m_what;
	public:
		AssemblerException(const std::string& msg) : m_what(msg), std::exception() {}
		const char* what() const override { return m_what.c_str(); }
	};

	class SymbolTable {
	public:
	
		SymbolTable(bool case_ignore = false) : m_case_ignore(false){}

		Symbol& lookup(istring name) {
			if (!m_case_ignore) {
				std::string u_case;
				std::transform(name.begin(), name.end(), u_case.begin(), ::toupper);
				name = u_case;
			}
			auto& ptr = m_table[name];
			if (!ptr) ptr.reset(new Symbol(name));
			return *ptr.get();
		}
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
		std::unordered_map<istring, std::unique_ptr<Symbol>> m_table;
		bool m_case_ignore;
	};
	enum class Kind : int {
		Error = 0,
		Value,
		Symbol,
		Add,
		Minus,
		Star,
		Equal,
		LBrack,
		RBrack,
		LArrow,
		RArrow,
		LParm,
		RParm,
		Comma,
		Comment,
		WhiteSpace,
		Eol,
		Eof
	};
	class Token {
	public:
		size_t size() const { return m_size; }
		Kind token() const { return m_token; }
		size_t pos() const { return m_pos; }
		Symbol* symbol() const {
			return (m_token != Kind::Symbol) ? nullptr : m_symbol;
		}
		int value() const {
			switch (m_token) {
			case Kind::Value: return m_value;
			case Kind::Symbol: return m_symbol->value();
			default:
				return 0;
			}
		}
	private:
		friend class Lexer;
		Token() : m_token(Kind::Error), m_size(0), m_pos(0), m_symbol(nullptr) {}
		union {
			Symbol* m_symbol;
			int m_value;
		};
		Kind m_token;
		size_t m_size;
		size_t m_pos;
	};

	class LexerException : public AssemblerException {
		Token m_token;
	public:
		LexerException(Token t, const std::string& msg) : AssemblerException(msg) {}
		const Token& token() const { return m_token; }
	};

	class Lexer {
	public:
		void reset() { m_pos = 0; }
		virtual void set_text(const char* str, size_t size) {
			reset();
			m_text = str;
			m_size = size;
		}
		std::vector<Token> tokens() {
			std::vector<Token> toks;
			toks.reserve(200);
			m_pos = 0;
			for (Token t = next(); t.token() != Kind::Eof; t = next()) {
				if (m_skip_whitespace && t.token() == Kind::WhiteSpace || t.token() == Kind::Eol) continue;
				if (m_skip_comment && t.token() == Kind::Comment) continue;
				toks.push_back(t);
			}
			return toks;
		}
		std::string token_text(const Token& token) {
			return std::string(m_text + token.pos(), token.size());
		}
	protected:
		static Kind oneCharLookup(char c);
		template<typename T>
		static inline bool isLineEnding(T v) { return v == '\n' || v == '\r'; }
		template<typename T>
		static inline bool isWhiteSpace(T v) { return v == ' ' || v == '\t'; }
		Token next() {
			Token t;
			t.m_pos = m_pos;
			t.m_token = Kind::Error;
			t.m_size=1;
			t.m_value = 0;
			int ch = 0, prev = 0;
			while (true){
				ch = m_pos < m_size ? (uint8_t)m_text[m_pos] : 0;
				Kind k = oneCharLookup(ch);
				if (t.m_token == Kind::Error) t.m_token = k;
				switch (t.m_token) {
				case Kind::Eol:// fix line ending
					if (prev != ch && isLineEnding(prev)) m_pos++;
					return t;
				case Kind::Symbol:
					if((k == Kind::Symbol || k == Kind::Value || ch == '_')) break;
					t.m_symbol = &m_table.lookup(istring(m_text + t.pos(), t.size()));
					return t;
				case Kind::Value:
					if (k == Kind::Value) {
						t.m_value *= m_value_base;
						switch (m_value_base) {
						case 8: 
							if (ch <'0' || ch > '7') throw SymbolTableException("value outside of range of base 8");
							t.m_value += ch - '0';
						case 10:
							if (ch <'0' || ch > '9') throw SymbolTableException("value outside of range of base 10");
							t.m_value += ch - '0';
						case 16: // not supported?
							if (ch < 'a' || ch > 'f' && ch <'0' || ch > '9') throw SymbolTableException("value outside of range of base 16");
							t.m_value += ch - '0';
						default:
							throw SymbolTableException("unsported base for value");
						}
						continue;
					}
					return t;
				case Kind::WhiteSpace:
					if (k == Kind::WhiteSpace) continue;
					return t;
				case Kind::Comment:
					if (k != Kind::Eol) continue;
					return t;
				default:
					throw SymbolTableException("error token base for value");
				}
				prev = ch;
				m_pos++;
				t.m_size++;
			}
			m_pos--; // backup
			return t;
		}
		
		SymbolTable m_table;
		bool m_skip_whitespace = true;
		bool m_skip_comment = true;
		int m_value_base;
		std::vector<Token> m_cache;
		const char* m_text;
		int m_prev;

		size_t m_size;
		size_t m_pos;
	};
	
	
	class Assembler {
		SymbolTable m_table;
		Lexer* m_lex;
		int  error(const char* str) {
			debug_out(str);
			return -1;
		}
	public:
		void parse(Lexer& lex) {
			m_lex = &lex;


		}
	private:

	};
};