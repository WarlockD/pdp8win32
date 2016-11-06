#pragma once
#include "global.h"
#include <Scintilla.h>
#include <SciLexer.h>

#include <set>

namespace util {
	inline std::string tolower(std::string s) {
		std::transform(s.begin(), s.end(), s.begin(), ::tolower);
		return s;
	}
	struct comp {
		bool operator() (const std::string& lhs, const std::string& rhs) const {
			return  _stricmp(lhs.c_str(), rhs.c_str()) < 0;
		}
	};
	// silly way to make a random seed
	inline unsigned int luai_makeseed() { return static_cast<unsigned int>(time(NULL)); }

	inline size_t luaS_hash(const char *str, size_t l, bool ignore_case, unsigned int seed=6667) {
		static constexpr int LUAI_HASHLIMIT = 5; // default from lua
		unsigned int h = seed ^ static_cast<unsigned int>(l);
		size_t step = (l >> LUAI_HASHLIMIT) + 1;
		if (ignore_case) {
			for (; l >= step; l -= step)
				h ^= ((h << 5) + (h >> 2) + static_cast<unsigned char>(::toupper(str[l - 1])));
		}
		else {
			for (; l >= step; l -= step)
				h ^= ((h << 5) + (h >> 2) + static_cast<unsigned char>(str[l - 1]));
		}
		
		return h;
	}
	inline size_t luaS_hash(const std::string& text, bool ignore_case, unsigned int seed = 6667) {
		return luaS_hash(text.c_str(), text.size(), ignore_case, seed);
	}
};
class Symbol {
	bool m_case_ignore;
	
	bool m_fixed;
	std::string m_name;
	mutable int m_line_defined; // HACKS
	mutable int m_value;
	std::set<int> m_lines_used;
public:
	Symbol(const std::string& name, bool fixed = false, bool ignore_case=true) :
		m_name(name), 
		m_line_defined(-1), 
		m_value(0) , 
		m_case_ignore(ignore_case),
		m_fixed(fixed) {
	}
	bool fixed() const { return m_fixed; }
	const std::string& name() const { return m_name; }
	bool case_ignore() const { return m_case_ignore; }
	int value() const { return m_value; }
	void value(int v) const{ m_value = v; }
	bool is_defined() const { return m_line_defined == -1; }
	int line_defined() const { return m_line_defined; }
	void define(int lineno) const { m_line_defined = lineno; }
	void add_used(int lineno) { m_lines_used.emplace(lineno); }
	void remove_used(int lineno) { m_lines_used.erase(lineno); }
	const std::set<int>& lines_used() const { return m_lines_used; }
	std::set<int>& lines_used()  { return m_lines_used; }
	bool operator<(const Symbol& other) const { 
		return m_case_ignore ? _stricmp(m_name.c_str(), other.m_name.c_str()) < 0 : m_name < other.m_name; 
	}
	bool operator==(const Symbol& other) const {
		return m_case_ignore ? _stricmp(m_name.c_str(), other.m_name.c_str()) == 0 : m_name == other.m_name;
	}
	bool operator!=(const Symbol& other) const { return !(*this == other); }
};
namespace std{
	template<>
	struct hash<Symbol> {
		size_t operator()(const Symbol& sym) const { 
			return util::luaS_hash(sym.name(), sym.case_ignore());
		}
	};
};
//template <class T>//, class TBase = CWindow, class TWinTraits = CDxAppWinTraits >
class MACRO8_Asembler : public CFrameWindowImpl<MACRO8_Asembler>    //CWindowImpl<AppWindow, CWindow, CDxAppWinTraits >
{
	static constexpr int SCE_STYLE_NORMAL = 11;
	static constexpr int SCE_STYLE_COMMENT = 12;
	static constexpr int SCE_STYLE_KEYWORD = 13;
	static constexpr int SCE_STYLE_SYMBOL = 14;
	static constexpr int SCE_STYLE_NUMBER = 15;
	static constexpr COLORREF s_black = 0x000000;
	static constexpr COLORREF s_white = 0xFFFFFF;
	static constexpr COLORREF s_blue = RGB(0, 0, 0xFF);
	static constexpr COLORREF s_purple = RGB(0xFF, 0, 0xFF);
	static constexpr COLORREF s_orange = RGB(0xFF, 128, 0);
	static constexpr const char* s_mem_ref_codes = "and tad isz dca jms jmp";
	static constexpr const char* s_group1_codes = "cla cll cma cml rar ral rtr rtl iac nop";
	typedef std::unordered_set<Symbol> t_symboltable;

	t_symboltable m_symbols;

	static constexpr const char* s_keywords = 
		""
		""
		"";
	BEGIN_MSG_MAP(AppWindow)
		MSG_WM_CREATE(OnCreate)
		MSG_WM_DESTROY(OnDestroy)
		MSG_WM_SIZE(OnSize)
		NOTIFY_HANDLER_EX(IDR_SCIEDIT, SCN_STYLENEEDED,  OnStyleNeeded)
		//MSG_WM_TIMER(OnTimer)
		//MSG_WM_KEYUP(OnKeyUp)
	//	MSG_WM_KEYDOWN(OnKeyDown)
	//	MSG_WM_KEYUP(OnKeyUp)
	//	MSG_WM_SIZE(OnSize)
		CHAIN_MSG_MAP(CFrameWindowImpl<MACRO8_Asembler>)
	END_MSG_MAP()
	// Return values for GetOperatorType
#define NO_OPERATOR     0
#define OPERATOR_1CHAR  1
#define OPERATOR_2CHAR  2


	/**
	*  IsIdentifierStart
	*
	*  Return true if the given char is a valid identifier first char
	*/

	static inline bool IsIdentifierStart(const int ch)
	{
		return (isalpha(ch) || (ch == '_') || (ch == '\\'));
	}


	/**
	*  IsIdentifierChar
	*
	*  Return true if the given char is a valid identifier char
	*/

	static inline bool IsIdentifierChar(const int ch)
	{
		return (isalnum(ch) || (ch == '_') || (ch == '@') || (ch == ':') || (ch == '.'));
	}


	/**
	*  GetOperatorType
	*
	*  Return:
	*  NO_OPERATOR     if char is not an operator
	*  OPERATOR_1CHAR  if the operator is one char long
	*  OPERATOR_2CHAR  if the operator is two chars long
	*/

	static inline int GetOperatorType(const int ch1, const int ch2)
	{
		int OpType = NO_OPERATOR;

		if ((ch1 == '+') || (ch1 == '-') || (ch1 == '*') || (ch1 == '/') || (ch1 == '#') ||
			(ch1 == '(') || (ch1 == ')') || (ch1 == '~') || (ch1 == '&') || (ch1 == '|') || (ch1 == ','))
			OpType = OPERATOR_1CHAR;

		else if ((ch1 == ch2) && (ch1 == '<' || ch1 == '>'))
			OpType = OPERATOR_2CHAR;

		return OpType;
	}


	/**
	*  IsBin
	*
	*  Return true if the given char is 0 or 1
	*/

	static inline bool IsBin(const int ch)
	{
		return (ch == '0') || (ch == '1');
	}


	/**
	*  IsDoxygenChar
	*
	*  Return true if the char may be part of a Doxygen keyword
	*/

	static inline bool IsDoxygenChar(const int ch)
	{
		return isalpha(ch) || (ch == '$') || (ch == '[') || (ch == ']') || (ch == '{') || (ch == '}');
	}

	HMODULE m_SciLexer_dll = nullptr;
	CWindow m_edit;
	std::string m_buffer;
	enum class ParseMode {
		OPCODE,
		ASSIGN,
		LABEL,
		SYMBOL
	};
	struct Token {
		int start;
		int size;
		int style;
	};
	void Parse(int start_pos, int end_pos, std::vector<Token>& tokens) {
#define CHANGE_STATE(STATE) { if(t.size>0) { tokens.emplace_back(t); t = { i, 0, (STATE) };}; t.style = (STATE); goto restart_state;}
#define CHANGE_STATE_CONTINUE(STATE) { if(t.size>0) { tokens.emplace_back(t); t = { i, 0, (STATE) };}; t.style = (STATE); continue;}
		Token t = { start_pos, 0, SCE_A68K_DEFAULT };
		int i = start_pos;
		while (i <= end_pos) {
			char ch = SendEditor(SCI_GETCHARAT, (WPARAM)i);
		restart_state: // love gotos/hate gotos this is is WAY more elegante than another embeded loop
			switch (t.style) {
			case SCE_A68K_DEFAULT:
				if (::isalpha(ch)) { m_buffer.clear(); CHANGE_STATE(SCE_A68K_IDENTIFIER); }
				if (::isdigit(ch)) { m_buffer.clear(); CHANGE_STATE(SCE_A68K_NUMBER_HEX); }
				if (ch == '/') CHANGE_STATE(SCE_A68K_COMMENT);

				break;
			case SCE_A68K_IDENTIFIER:
				if (::isalnum(ch)) m_buffer.push_back(ch);
				else {
					if (ch == ',') {
						t.style = SCE_A68K_LABEL;
						t.size++;
						i++;
						CHANGE_STATE_CONTINUE(SCE_A68K_DEFAULT);
					 }else
					//if(mode == ParseMode::ASSIGN)
					CHANGE_STATE(SCE_A68K_DEFAULT);
				}
				break;
			case SCE_A68K_NUMBER_HEX:
				if (::isdigit(ch)) m_buffer.push_back(ch);
				else {
					/*
					if (mode == ParseMode::ASSIGN) {

					auto& symbol = m_symbols.emplace(Symbol(equ));
					t_symboltable::iterator& it = symbol.first;
					if (!it->is_defined()) {
					int value = strtol(m_buffer.data(), nullptr, 8);
					it->value(value);
					it->define(line_number + 1);
					}
					}
					*/
					CHANGE_STATE(SCE_A68K_DEFAULT);
				}
				break;
			case SCE_A68K_COMMENT:
				if (ch == '\n') {
					CHANGE_STATE(SCE_A68K_DEFAULT);
				}
				break;
			}
			t.size++;
			i++;
		}
	}
	LRESULT OnStyleNeeded(LPNMHDR nmhdr) {
		SCNotification* notify = (SCNotification*)nmhdr;
		ParseMode mode = ParseMode::OPCODE;
		int ended = SendEditor(SCI_GETENDSTYLED);
		const int line_number = SendEditor(SCI_LINEFROMPOSITION, ended);
		const int start_pos = SendEditor(SCI_POSITIONFROMLINE, line_number);
		const int end_pos = notify->position;

		m_buffer.clear();
		std::vector<Token> tokens;
		Parse(start_pos, end_pos, tokens);
		for (auto& t: tokens) {
			SendEditor(SCI_STARTSTYLING, t.start);
			SendEditor(SCI_SETSTYLING, t.size, t.style);
		}
	
		return TRUE;
	}
	template<typename WTYPE, typename LTYPE>
	LRESULT SendEditor(UINT Msg, WTYPE wParam, LTYPE lParam) {
		return m_edit.SendMessageA(Msg, (WPARAM)wParam, (LPARAM)lParam);
	}
	template<typename WTYPE>
	LRESULT SendEditor(UINT Msg, WTYPE wParam) {
		return m_edit.SendMessageA(Msg, (WPARAM)wParam);
	}
	LRESULT SendEditor(UINT Msg) {
		return m_edit.SendMessageA(Msg);
	}
	void OnSize(UINT func, CSize size) {
		m_edit.ResizeClient(size.cx, size.cy, true);
	}
	
	LRESULT OnCreate(LPCREATESTRUCT lpcs)
	{
		CreateSimpleToolBar();
		m_SciLexer_dll = LoadLibrary("SciLexer.DLL");
		
		if (m_SciLexer_dll == NULL)
		{
			MessageBoxA("The Scintilla DLL could not be loaded.", "Error loading Scintilla", MB_OK | MB_ICONERROR);
			PostQuitMessage(0);
		}
		else {
			CRect rect;
			GetClientRect(&rect);
			m_edit.Create("Scintilla", m_hWnd, rect, "", WS_CHILD | WS_VISIBLE | WS_TABSTOP | WS_CLIPCHILDREN, 0, (HMENU)IDR_SCIEDIT);
			ATLASSERT(::IsWindow(m_edit.m_hWnd)); 
				SendEditor(SCLEX_A68K, SCLEX_A68K);
			m_edit.SendMessageA(SCI_SETLEXER, SCLEX_CONTAINER);
			m_edit.SendMessageA(SCI_STYLESETFORE, STYLE_DEFAULT, s_black);
			m_edit.SendMessageA(SCI_STYLESETBACK, STYLE_DEFAULT, s_white);
			m_edit.SendMessageA(SCI_STYLECLEARALL);
			m_edit.SendMessageA(SCI_STYLESETFORE, SCE_A68K_IDENTIFIER, s_black);
			m_edit.SendMessageA(SCI_STYLESETFORE, SCE_A68K_COMMENT, RGB(0x00,0xFF,0x00));
			m_edit.SendMessageA(SCI_STYLESETFORE, SCE_A68K_NUMBER_HEX, s_purple);
			m_edit.SendMessageA(SCI_STYLESETFORE, SCE_A68K_LABEL, RGB(0x20,0x50,0x20));
			std::ifstream test("..\\PDP8misc\\tests\\src\\MAINDEC-08-D1GB-D.pal");
			assert(test.good());
			test.seekg(0, std::ios::end);
			size_t size = test.tellg();
			test.seekg(0, std::ios::beg);
			std::vector<char> buffer(size);
			test.read(buffer.data(), size);
			SendEditor(SCI_SETTEXT, size, buffer.data());
		}

		return 0;
	}
	void OnDestroy() {
		//KillTimer(2);
		//if (m_memory_view.m_hWnd) m_memory_view.DestroyWindow();

		PostQuitMessage(0);
		SetMsgHandled(false);
	}
};