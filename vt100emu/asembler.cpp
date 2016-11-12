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

namespace macro8 {
	class istring_table {
		std::vector<istring::_istring*> m_string_table;
		std::multimap<size_t, char*> m_free_memory;
		std::vector<char> m_storage;
		std::unordered_set<const istring*> m_istrings;
		size_t m_strings_used;
		void remove(istring::_istring* s) {
			istring::_istring** p = m_string_table.data() + lmod(s->hash, m_string_table.size());
			while (*p != s) p = &(*p)->next; // find previous element
			*p = (*p)->next;  /* remove element from its list */
			deallocate(s);
			m_strings_used--;
		}
		void resize_string_table(size_t newsize) {
			if (newsize > m_string_table.size()) {
				m_string_table.resize(newsize); // auto nulls
				//for (int i = m_string_table.size(); i < newsize; i++)
					//m_string_table
					//tb->hash[i] = NULL;
			}
			// rehash
			for (size_t i = 0; i < m_string_table.size(); i++) {  /* rehash */
				istring::_istring* p = m_string_table[i];
				m_string_table[i] = nullptr;
				while (p) {  /* for each node in the list */
					istring::_istring* hnext = p->next;  /* save next */
					size_t h = lmod(p->hash, newsize);  /* new position */
					p->next = m_string_table[i];  /* chain it */
					m_string_table[i] = p;
					p = hnext;
				}
			}
			if (newsize < m_string_table.size()) {// vanishing slice should be empty 
				m_string_table.resize(newsize); 
			}
		}
		istring::_istring*  intern(const char* str, size_t l) {
			istring::_istring* ts;
			size_t h = luaS_hash(str, l);
			istring::_istring** list = &m_string_table[lmod(h, m_string_table.size())];
			for (ts = *list; ts != nullptr; ts = ts->next) {
				if (l == ts->size && ::memcmp(str, ts->str, sizeof(char)*l) == 0) ts; // found!
			}
			// not found, must create
			if (m_strings_used >= m_string_table.size() && m_string_table <= (std::numeric_limits<int>::max() / 2)) {
				resize_string_table(m_string_table.size() * 2);
				list = &m_string_table[lmod(h, m_string_table.size())];  /* recompute with new size */
			}
			ts = allocate(l);
			ts->size = l;
			ts->hash = h;
			::memcpy(ts->str, str, sizeof(char)*l);
			ts->str[l] = 0;
			ts->next = *list;
			*list = ts; // link
			m_strings_used++;
			return ts;
		}

	

		void deallocate(istring::_istring* ptr) {
			char* cptr = reinterpret_cast<char*>(ptr);
			size_t size = sizeof(istring::_istring) + ptr->size;
			m_free_memory.emplace(std::make_pair(size, cptr));
#ifdef _DEBUG
			::memset(cptr, 0, sizeof(istring::_istring) + ptr->size);// we do this just for debugging
#endif
		}
		istring::_istring* allocate(size_t str_size) {
			size_t size = sizeof(istring::_istring) + str_size;

			while (true) {
				for (auto it = m_free_memory.begin(); it != m_free_memory.end(); it++) {
					if (it->first >= size) {
						char* ptr = it->second;
						m_free_memory.erase(it);
						m_free_memory.emplace(std::make_pair(it->second - size, ptr + size));
						return reinterpret_cast<istring::_istring*>(ptr);
					}
				}
				// couldn't find any free space so we have to remake it all
				if (!collect_garbage()) { // nothing to collect so we got to defragment and resize
					std::vector<char> new_storage(m_storage.size() * 2);
					char* start = new_storage.data();
					size_t used = 0;
					for (auto it = m_istrings.begin(); it != m_istrings.end(); it++) {
						size_t it_size = sizeof(istring::_istring) + it->first->size;
						istring::_istring* i = reinterpret_cast<istring::_istring*>(new_storage.data() + used);
						::memcpy(i, it->first, it_size);
						used += it_size;
						n_string_table.emplace(i);
						for (auto o : it->second) {
							assert(o);
							istring* is = const_cast<istring*>(o);
							is->m_str = i;
						}
						n_istrings[i] = std::move(it->second); // save on alloc
					}
					s_istrings = std::move(n_istrings);
					s_istring_table = std::move(n_string_table);
					m_free_memory.clear();
					m_free_memory.emplace(std::make_pair(used, new_storage.data() + used));
				}
			}
			assert(false);
			return nullptr;
		}

	public:
		istring_table() : m_storage(4096) { m_free_memory.emplace(std::make_pair(m_storage.size(), m_storage.data())); }
		const istring::_istring* intern(const char* str, size_t size) {
			fake search = { size, luaS_hash(str,size), str };
			auto it = s_istring_table.find(&search.real);
			if (it != s_istring_table.end()) return *it;
			istring::_istring* ptr = reinterpret_cast< istring::_istring*>(new char[sizeof(istring::_istring) + size]);
			ptr->hash = search.real.hash;
			ptr->size = size;
			::memcpy(ptr->str, str, sizeof(char) * size);
			ptr->str[size] = 0;
			s_istring_table.emplace(ptr);
			return ptr;
		}
		size_t collect_garbage() {
			size_t items_deleted = 0;
			for (auto it = s_istrings.begin(); it != s_istrings.end(); ) {
				if (it->second.empty()) {
					istring::_istring* ptr = const_cast<istring::_istring*>(it->first);
					deallocate(ptr);
					items_deleted++;
					it = s_istrings.erase(it);
				}
				else ++it;
			}
			return items_deleted;
		}
		// ugh... this is slow
		void istring_assign(istring* i, const istring::_istring* s) {
			if (i->m_str != s) {
				if (i->m_str) s_istrings[i->m_str].erase(i);
				if(s) s_istrings[s].emplace(i);
				i->m_str = s;
			}
		}
	};
	static istring_table m_istring_table;
	Kind Lexer::oneCharLookup(char c) { return s_char_lookup.at((uint8_t)c); }
	// removes all strings not being used
	void istring::collect_garbage() { m_istring_table.collect_garbage(); }
	void istring::swap(istring&& s);	// swap interface
	{
		if (m_str != s.m_str) {
			auto temp = m_str;

			istring temp(*this);
			assign(s);
			s.assign(temp);
		}
	}
	void istring::clear() {
		if (m_str) {
			m_str = nullptr;
			m_istring_table.istring_ref(this);
		}
	}
	void istring::assign(const istring& str) {
		if (m_str != str.m_str) {

		}
		


	}
	void istring::assign(const char* str, size_t size) {

	}
	void istring::assign(const std::string& str) {
		if (str.empty()) {
			if(m_str) s_istring_used.erase(this);
			m_str = nullptr;
		}
		else {
			if(!m_str) s_istring_used.emplace(this);
			auto it = s_istring_table.find(&str);
			if (it != s_istring_table.end()) m_str = (*it);
			else {
				m_str = new std::string(str);
				s_istring_table.emplace(m_str);
			}
		}
	}
	void istring::assign(std::string&& str) {
		if (str.empty()) {
			if (m_str) s_istring_used.erase(this);
			m_str = nullptr;
		}
		else {
			if (!m_str) s_istring_used.emplace(this);
			auto it = s_istring_table.find(&str);
			if (it != s_istring_table.end()) m_str = (*it);
			else {
				m_str = new std::string(std::move(str));
				s_istring_table.emplace(m_str);
			}
		}
	}
};