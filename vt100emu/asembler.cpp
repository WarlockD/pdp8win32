#include "asembler_editor.h"
#include <random>
#include <map>
#ifdef min
#undef min
#undef max
#endif
/*

void MACRO8_Asembler::ColouriseMacro8kDoc(
	unsigned int startPos, int length,
	int initStyle,
	WordList *keywordlists[],
	Accessor &styler{

}
*/
namespace {
	
	
	

	std::array<macro8::Kind, 256> CharsLookupSetup() {
		std::array<macro8::Kind, 256> m_lookup;
		for (size_t i = 0; i < 256; i++) {
			if (::isalpha(i)) m_lookup[i] = macro8::Kind::Symbol;
			else if (::isdigit(i)) m_lookup[i] = macro8::Kind::Value;
			else m_lookup[i] = macro8::Kind::Error;
		}
		m_lookup[0] = macro8::Kind::Eof;
		m_lookup['\n'] = macro8::Kind::Eol;
		m_lookup['\r'] = macro8::Kind::Eol;
		m_lookup[' '] = macro8::Kind::WhiteSpace;
		m_lookup['\t'] = macro8::Kind::WhiteSpace;
		m_lookup['['] = macro8::Kind::LBrack;
		m_lookup[']'] = macro8::Kind::RBrack;
		m_lookup['<'] = macro8::Kind::LArrow;
		m_lookup['>'] = macro8::Kind::RArrow;
		m_lookup['('] = macro8::Kind::LParm;
		m_lookup[')'] = macro8::Kind::RParm;
		m_lookup['+'] = macro8::Kind::Add;
		m_lookup['-'] = macro8::Kind::Minus;
		m_lookup['*'] = macro8::Kind::Star;
		m_lookup['='] = macro8::Kind::Equal;
		m_lookup[','] = macro8::Kind::Comma;
		return m_lookup;
	}
	auto s_char_lookup = CharsLookupSetup();
	namespace _private {
		template<typename T>
		static T generate_seed() {
			// Seed with a real random value, if available
			std::random_device r; // supposed its a seed?
			std::default_random_engine e(r());
			std::uniform_int_distribution<T> uniform_dist(std::numeric_limits<T>::min(), std::numeric_limits<T>::max());
			return uniform_dist(e);
		}
	};
	

	static size_t luaS_hash(const char *str, size_t l) {
		static const size_t seed = _private::generate_seed<size_t>();
		constexpr size_t LUAI_HASHLIMIT = 5;
		size_t h = seed ^ static_cast<size_t>(l);
		size_t step = (l >> LUAI_HASHLIMIT) + 1;
		for (; l >= step; l -= step)
			h ^= ((h << 5) + (h >> 2) + static_cast<uint8_t>(str[l - 1]));
		return h;
	}
	/*
	** 'module' operation for hashing (size is always a power of 2)
	*/
	template<typename T>
	inline constexpr T lmod(T s, T size) { return  (s) & ((size)-1); }



#define twoto(x)	(1<<(x))
#define sizenode(t)	(twoto((t)->lsizenode))
	template<typename T>
	class lua_hash {
		struct {};
		std::list<int*> test;
	};
};
namespace util {
	// copied from Lua 5.3.3
	class StringTable {
		struct istring {
			// for compiler errors
			istring(const istring&) = delete;
			istring(istring&&) = delete;
			void mark() { _size |= (0x01 << 24); }
			void  unmark() { _size &= ~(0x01 << 24); }
			bool marked() const { return _size & 0xFF000000 != 0; }
			bool fixed() const { return _size & 0x10000000 != 0; }
			size_t size() const { return _size & 0x00FFFFFF; }
			size_t _size;
			char str[1];
		};
		union istring_find {
			struct {
				size_t size;
				const char* str;
			} find;
			istring org;
		};
		struct hasher {
			size_t operator()(const istring* l) const { return luaS_hash(l->str, l->size()); };
		};
		struct equaler {
			bool operator()(const istring* l, const istring* r) const { return l->size() == r->size() && ::memcmp(l->str, r->str, sizeof(char)*l->size()) == 0; };
		};
		std::unordered_set<istring*, hasher, equaler> m_string_table;
		std::unordered_set<const String*> m_istrings;
		size_t m_strings_used = 0;


		void clear_marks() {
			for (auto a : m_string_table) a->unmark(); // a->size &= ~(0x10 << 24);
		}
	public:
		istring*  intern(const char* str, size_t l) {
			istring_find search = { l , str };
			auto it = m_string_table.find(&search.org);
			if (it != m_string_table.end()) return *it;
			istring* ts = reinterpret_cast<istring*>(new char[sizeof(istring) + l]);
			ts->_size = l & 0x00FFFFFF;
			::memcpy(ts->str, str, sizeof(char)*l);
			ts->str[l] = 0;
			m_string_table.emplace(ts);
			return ts;
		}
		static istring s_empty;
		StringTable() {
			m_string_table.emplace(&s_empty);
		}
		void make_perm(istring* s) {
			s->_size |= (0x10 << 24);
		}
		void collect_garbage() {
			for (auto s : m_istrings) {
				istring* str = const_cast<istring*>(reinterpret_cast<const istring*>(s->c_str() - 4));
				str->mark();
			}
			for (auto it = m_string_table.begin(); it != m_string_table.end();) {
				istring* str = (*it);
				if (str->fixed() || str->marked()) {
					it++;
					str->unmark();
				}
				else {
					it = m_string_table.erase(it);
					delete str;
				}
			}
		}
		void add_string(const String* str) {
			if (str->c_str() != StringTable::s_empty.str)
				m_istrings.emplace(str);
		}
		void del_string(const String* str) {
			if (str->c_str() != StringTable::s_empty.str)
				m_istrings.erase(str);
		}
		void swap_string(String* l, String* r) {
			if (l->m_str != r->m_str) {
				if (l->m_str == s_empty.str) { m_istrings.emplace(l); m_istrings.erase(r); }
				else if (r->m_str == s_empty.str) { m_istrings.emplace(r); m_istrings.erase(l); }
				std::swap(l->m_str, r->m_str);
			}
		}
		void assign_string(String* l, const char* r) {
			if (l->m_str != r) {
				if (l->m_str == s_empty.str)  m_istrings.emplace(l);
				else if (r == s_empty.str)  m_istrings.erase(l);
				l->m_str = r;
			}
		}
		static const char* cast(const istring* str) { return str->str; }
	};
	StringTable::istring StringTable::s_empty = { (0x10 << 24), 0 };
	static StringTable s_string_table;

	String::String() : m_str(StringTable::s_empty.str) { }
	String::String(const String& a) : m_str(a.m_str) { s_string_table.add_string(this); }
	String::String(String&& a) : String() { s_string_table.swap_string(this, &a); }
	String& String::operator=(const String& a) { s_string_table.assign_string(this, a.m_str); return *this; }
	String& String::operator=(String&& a) { s_string_table.swap_string(this, &a); return *this; }
	String::~String() { s_string_table.del_string(this); }

	void String::assign(const char* str, size_t size) {
		auto p = s_string_table.intern(str, size);
		s_string_table.assign_string(this, p->str);
	}

	String::String(const char* str, size_t l) : m_str(s_string_table.intern(str, l)->str) { s_string_table.add_string(this); }


};

namespace macro8 {

};