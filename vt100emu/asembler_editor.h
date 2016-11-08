#pragma once
#include "global.h"
#include "maco8_asembler.h"
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
	macro8::SymbolTable m_table;

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
	std::vector<char> m_buffer;


	LRESULT OnStyleNeeded(LPNMHDR nmhdr) {
		SCNotification* notify = (SCNotification*)nmhdr;
		int start_pos = SendEditor(SCI_GETENDSTYLED); // from
		int end_pos = notify->position; // too
		// but we need the begining of the line to properly style it
		int line_number = SendEditor(SCI_LINEFROMPOSITION, start_pos);
		start_pos = SendEditor(SCI_POSITIONFROMLINE, line_number);
		m_buffer.resize(end_pos - start_pos + 1);
		Sci_TextRange range = { { start_pos, end_pos }, m_buffer.data() };
		//const int line_len2 = SendEditor(SCI_GETLINE, line_number, buffer.data());
		const int line_len2 = SendEditor(SCI_GETTEXTRANGE, 0, &range);
		m_buffer[m_buffer.size()-1] = 0;

		macro8::Lexer lex;
		lex.parse(m_buffer.data(), m_buffer.size());
		for (auto& t: lex.tokens()) {
			SendEditor(SCI_STARTSTYLING, start_pos+t.pos());
			int style = 0;
			switch (t.token()) {
			case macro8::Lexer::COMMENT: style = SCE_A68K_COMMENT; break;
			case macro8::Lexer::NUMBER: style = SCE_A68K_NUMBER_HEX; break;
			case macro8::Lexer::SYMBOL: style = SCE_A68K_IDENTIFIER; break;
			default: style = SCE_A68K_DEFAULT; break;
			}
			SendEditor(SCI_SETSTYLING, t.size(), style);
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