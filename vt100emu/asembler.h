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
	int m_line_defined;
	int m_value;
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
	void value(int v) { m_value = v; }
	bool is_defined() const { return m_line_defined == -1; }
	int line_defined() const { return m_line_defined; }
	void define(int lineno) { m_line_defined = lineno; }
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
	static constexpr int SCE_STYLE_ORANGE = 11;
	static constexpr int SCE_STYLE_PURPLE = 12;
	static constexpr int SCE_STYLE_BLUE = 13;
	static constexpr int SCE_STYLE_BLACK = 14;
	static constexpr COLORREF s_black = 0x000000;
	static constexpr COLORREF s_white = 0xFFFFFF;
	static constexpr COLORREF s_blue = RGB(0, 0, 0xFF);
	static constexpr COLORREF s_purple = RGB(0xFF, 0, 0xFF);
	static constexpr COLORREF s_orange = RGB(0xFF, 128, 0);
	static constexpr const char* s_mem_ref_codes = "and tad isz dca jms jmp";
	static constexpr const char* s_group1_codes = "cla cll cma cml rar ral rtr rtl iac nop";
	
	std::unordered_set<Symbol> m_symbols;

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
	LRESULT OnStyleNeeded(LPNMHDR nmhdr) {
		SCNotification* notify = (SCNotification*)nmhdr;
		const int line_number = SendEditor(SCI_LINEFROMPOSITION, SendEditor(SCI_GETENDSTYLED));
		const int start_pos = SendEditor(SCI_POSITIONFROMLINE, (WPARAM)line_number);
		const int end_pos = notify->position;
		int line_length = SendEditor(SCI_LINELENGTH, (WPARAM)line_number);
		int state;
		// The SCI_STARTSTYLING here is important
		SendEditor(SCI_STARTSTYLING, start_pos);

		if (start_pos == 0) {
			SendEditor(SCI_SETSTYLING, start_pos, SCE_STYLE_BLACK);
				state = SCE_STYLE_BLACK;
		}
		else {
			state = SendEditor(SCI_GETSTYLEAT, start_pos);
		}

		for (int i = start_pos; i <= end_pos; i++) {
			char ch = SendEditor(SCI_GETCHARAT, (WPARAM)i);
			if (ch == '\\') {
				SendEditor(SCI_STARTSTYLING, i);
				SendEditor(SCI_SETSTYLING, end_pos-i, SCE_STYLE_PURPLE);
			}
			//else if (ch == '\n') {
			//	SendEditor(SCI_SETSTYLING, start_pos, SCE_STYLE_BLACK);
			//}
		}
		return TRUE;
	}
	LRESULT SendEditor(UINT Msg, WPARAM wParam = 0, LPARAM lParam = 0) {
		return m_edit.SendMessageA(Msg, wParam, lParam);
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
			m_edit.SendMessageA(SCI_SETLEXER, SCLEX_CONTAINER);
			m_edit.SendMessageA(SCI_STYLESETFORE, STYLE_DEFAULT, s_black);
			m_edit.SendMessageA(SCI_STYLESETBACK, STYLE_DEFAULT, s_white);
			m_edit.SendMessageA(SCI_STYLECLEARALL);
			m_edit.SendMessageA(SCI_STYLESETFORE, SCE_STYLE_BLACK, s_black);
			m_edit.SendMessageA(SCI_STYLESETFORE, SCE_STYLE_ORANGE, s_orange);
			m_edit.SendMessageA(SCI_STYLESETFORE, SCE_STYLE_PURPLE, s_purple);
			m_edit.SendMessageA(SCI_STYLESETFORE, SCE_STYLE_BLUE, s_blue);
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