#pragma once
#include "global.h"
#include <varargs.h>

// attribute bits for debug_view_char.attrib
constexpr uint8_t DCA_NORMAL = 0x00;     // black on white

constexpr uint8_t DCA_CHANGED = 0x01;     // red foreground
constexpr uint8_t DCA_COMMENT = 0x02;     // green foreground
constexpr uint8_t DCA_INVALID = 0x04;     // dark blue foreground
constexpr uint8_t DCA_DISABLED = 0x08;     // darker foreground

constexpr uint8_t DCA_SELECTED = 0x10;     // light red background
constexpr uint8_t DCA_ANCILLARY = 0x20;     // grey background
constexpr uint8_t DCA_CURRENT = 0x40;     // yellow background
constexpr uint8_t DCA_VISITED = 0x80;     // light blue background
constexpr uint8_t DCA_FOREGROUND_MASK = DCA_CHANGED | DCA_INVALID | DCA_DISABLED | DCA_COMMENT;
constexpr uint8_t DCA_BACKGROUND_MASK = DCA_SELECTED | DCA_ANCILLARY | DCA_CURRENT | DCA_VISITED;


static constexpr inline bool DCA_BACKGROUND_EQUAL(uint8_t a1, uint8_t a2) { return (DCA_BACKGROUND_MASK & a1) == (DCA_BACKGROUND_MASK & a2); }
static constexpr inline bool DCA_FOREGROUND_EQUAL(uint8_t a1, uint8_t a2) { return (DCA_FOREGROUND_MASK & a1) == (DCA_FOREGROUND_MASK & a2); }

struct char_attrib { uint8_t byte; uint8_t attrib; char_attrib(uint8_t b, uint8_t a) : byte(b), attrib(a) {} };
static inline std::pair<COLORREF, COLORREF> GetAttribColor(uint8_t attrib) {
	COLORREF fgcolor = RGB(0x00, 0x00, 0x00);
	COLORREF bgcolor = RGB(0xff, 0xff, 0xff);
	if (attrib & DCA_VISITED) bgcolor = RGB(0xc6, 0xe2, 0xff);
	if (attrib & DCA_ANCILLARY) bgcolor = RGB(0xe0, 0xe0, 0xe0);
	if (attrib & DCA_SELECTED) bgcolor = RGB(0xff, 0x80, 0x80);
	if (attrib & DCA_CURRENT) bgcolor = RGB(0xff, 0xff, 0x00);
	if ((attrib & DCA_SELECTED) && (attrib & DCA_CURRENT)) bgcolor = RGB(0xff, 0xc0, 0x80);
	if (attrib & DCA_CHANGED) fgcolor = RGB(0xff, 0x00, 0x00);
	if (attrib & DCA_INVALID) fgcolor = RGB(0x00, 0x00, 0xff);
	if (attrib & DCA_DISABLED) fgcolor = RGB((GetRValue(fgcolor) + GetRValue(bgcolor)) / 2, (GetGValue(fgcolor) + GetGValue(bgcolor)) / 2, (GetBValue(fgcolor) + GetBValue(bgcolor)) / 2);
	if (attrib & DCA_COMMENT) fgcolor = RGB(0x00, 0x80, 0x00);
	return std::make_pair(fgcolor, bgcolor);
}
namespace util {
	inline static void error(const char* fmt, ...) {
		va_list va;
		char buffer[1024];
		va_start(va, fmt);
		vsnprintf_s(buffer, 1023, fmt, va);
		va_end(va);
		::OutputDebugString(buffer);
	}
};
//class MemoryViewer : public CDoubleBufferWindowImpl<MemoryViewer, CWindow, CFrameWinTraits > 
class MemoryViewer : public CWindowImpl<MemoryViewer, CWindow, CFrameWinTraits >
{
	std::vector<std::vector<CRect>> m_text_rects;
//#define CURSOR_ENABLE
	static constexpr int WM_EDITDONE = WM_APP + 50;
	static constexpr int WM_EDITCANCLED = WM_APP + 51;
	class CWordEdit : public CWindowImpl< CWordEdit, CEdit, CControlWinTraits > {
	public:
		DECLARE_WND_SUPERCLASS(_T("CWordEdit"), CEdit::GetWndClassName())
		BEGIN_MSG_MAP(CWordEdit)
			MSG_WM_CHAR(OnChar);
		END_MSG_MAP()
		
		BOOL SubclassWindow(HWND hWnd)
		{
			ATLASSERT(m_hWnd == NULL);
			ATLASSERT(::IsWindow(hWnd));
			BOOL bRet = CWindowImpl< CWordEdit, CEdit, CControlWinTraits >::SubclassWindow(hWnd);
			//if (bRet) _Init();
			return bRet;
		}
		//DECLARE_WND_CLASS("CWordEdit")
		//LRESULT OnChar(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
		HWND m_parent;
		uint16_t* m_value;
		void ShowText(LPCRECT rect, uint16_t* value) {
			error = false;
			m_value = value;
			char buffer[128];
			int count = sprintf_s(buffer, "%4.4o", *m_value);
			SetWindowTextA(buffer);
			SendMessage(EM_SETSEL, 0, count);
			MoveWindow(rect);
			ShowWindow(SW_SHOW);
			SetFocus();
		}
		bool error = false;
		void OnChar(TCHAR ch, UINT lparm, UINT rparm) {
			if (ch >= '0' && ch <= '7') {
				if (error) {
					DeleteObject((HBRUSH)SetClassLong(m_hWnd, GCL_HBRBACKGROUND, (LONG)CreateSolidBrush(RGB(0xFF, 0xFF, 0xFF))));
					error = false;
					InvalidateRect(false);
				}
			}
			else if (ch == '\r') {
				char buffer[128];
				char* ending;
				GetWindowTextA(buffer, 128);
				int value = strtol(buffer, &ending, 8);
				if (ending == buffer) {
					MessageBeep(MB_OK);
				}
				else {
					*m_value = value & 0xFFF;
					SetMsgHandled(true);
					ShowWindow(SW_HIDE);
					return;
				}
			}
			else {
				if (!error) {
					DeleteObject((HBRUSH)SetClassLong(m_hWnd, GCL_HBRBACKGROUND, (LONG)CreateSolidBrush(RGB(0xFF, 0x00, 0x00))));
					error = true;
					InvalidateRect(false);
				}
			}
			SetMsgHandled(false);
		}
	};
	static constexpr int ID_NEDIT = 1;
	class dc_info {
		uint8_t m_attrib = 0;
		COLORREF fgcolor;
		COLORREF bgcolor;
		COLORREF default_bgcolor;
	public:
		uint8_t attrib() const { return m_attrib; }
		COLORREF background() const { return bgcolor; }
		COLORREF foreground() const { return fgcolor; }

		bool operator==(const dc_info& r) const { return m_attrib == r.m_attrib; }
		bool operator!=(const dc_info& r) const { return !(*this == r); }
		dc_info(COLORREF default_bg = RGB(0xff, 0xff, 0xff)) :
			m_attrib(DCA_NORMAL),
			fgcolor(RGB(0x00, 0x00, 0x00)),
			bgcolor(default_bg),
			default_bgcolor(default_bg)
		{}
		bool flag_set(uint8_t attr) {
			return attrib(m_attrib | attr);
		}
		bool flag_clear(uint8_t attr) {
			return attrib(m_attrib & ~attr);
		}
		bool attrib(uint8_t last_attrib) {
			if (m_attrib != last_attrib) {
				auto color = GetAttribColor(last_attrib);
				bgcolor = (last_attrib&DCA_BACKGROUND_MASK) == 0 ? default_bgcolor : color.second;
				fgcolor = color.first;
				m_attrib = last_attrib;
				return true;
			}
			return false;
		}
		static CRect DrawString(HDC hdc, CPoint& pos, const char* text, size_t tsize, COLORREF fg, COLORREF bg) {
			CDCHandle dc(hdc);
			CSize size;
			dc.GetTextExtent(text, tsize, &size);
			CRect rect = CRect(pos, size);//ETO_OPAQUE ETO_CLIPPED
			CBrush brush;
			brush.CreateSolidBrush(bg);
			dc.SetBkColor(bg);
			dc.FillRect(&rect, brush);
			dc.SetTextColor(fg);
			//dc.TextOutA(rect.left, rect.top, text, tsize);
			dc.ExtTextOut(rect.left, rect.top, 0, nullptr, text, tsize, nullptr);
			//&rect																	 
			//::FillRect(hdc,&rect,)																 
			//::ExtTextOut(hdc,rect.left, rect.top, ETO_OPAQUE, &rect, text, tsize, nullptr);
			pos.x = rect.right;
			return rect;
		}
		CRect DrawString(HDC hdc, CPoint& pos, const char* text, size_t tsize) const {
			return DrawString(hdc, pos, text, tsize, fgcolor, bgcolor);
		}
	};
	struct word_info {
		int m_digit_base = 8;
		dc_info attrib;
		uint16_t* m_address;
		char text[16];
		size_t text_size;
		CRect rect;
		word_info(uint16_t* address, int digit_base = 8) : m_address(address), m_digit_base(digit_base) {
			text_size = sprintf_s(text, "%4.4o", *m_address);
		}
		uint16_t* address() const { return m_address; }
		void address(uint16_t* addr) {
			if (addr != m_address) {
				m_address = addr;
				text_size = sprintf_s(text, "%4.4o", *m_address);
			}
		}
		void update_from_text() {
			long i = strtol(text, nullptr, m_digit_base);
			if (m_address) *m_address = i;
		}
		void update_from_address() {
			int value = (m_address) ? *m_address : 0;
			text_size = sprintf_s(text, "%4.4o", *m_address);
		}
		CRect draw(HDC hdc, CPoint& pos) {
			int value = m_address ? *m_address : 0;
			return rect = attrib.DrawString(hdc, pos, text, text_size);
		}
	};
	struct line_t {
		size_t lineno = 0;
		size_t offset = 0;
		CRect rect;
		std::vector<word_info> line;

		CRect draw(HDC hdc, CPoint& pos, int word_space = 8) {
			CDCHandle dc(hdc);
			char buffer[64];
			int count = sprintf_s(buffer, "%5.5o : ", (uint16_t)offset);
			rect = dc_info::DrawString(hdc, pos, buffer, count, RGB(0x00, 0x00, 0x00), RGB(0xFF, 0xFF, 0xFF));
			for (auto& i : line) {
				i.draw(hdc, pos);
				pos.x = i.rect.right + word_space;
			}
			rect.right = line.back().rect.right;
			return rect;
		}
		void swap(line_t&& other) {
			std::swap(line, other.line);
		}
	};
	struct line_view_t {
		size_t m_words_per_line;
		uint16_t *m_data;
		size_t m_size;
		std::vector<line_t> m_lines;
	public:
		line_view_t(size_t words_per_line = 8) : m_data(nullptr), m_size(0), m_words_per_line(words_per_line) { }
		void set_data(uint16_t* data, size_t size) {
			assert(data);
			if (data != m_data) {
				m_data = data;
				m_size = size;
				size_t linecount = m_size / m_words_per_line;
				m_lines.resize(linecount);
				size_t lno = 0;
				for (size_t l = 0; l < linecount; l++) {
					auto& ll = m_lines[l];
					ll.lineno = l;
					ll.offset = l* m_words_per_line;
					
					for (size_t i = 0; i < m_words_per_line; i++) {
						ll.line.emplace_back(data++);
						if (l & 1 == 0) ll.line.back().attrib.attrib(DCA_ANCILLARY);
					}
				}
			}
		}
	};

	BEGIN_MSG_MAP(TerminalView)
		MSG_WM_CREATE(OnCreate)
		MSG_WM_DESTROY(OnDestroy)
	//COMMAND_ID_HANDLER(ID_NEDIT, OnEdit)
	//MSG_WM_LBUTTONDBLCLK(OnLButtonClick)
	COMMAND_HANDLER(ID_NEDIT, EN_KILLFOCUS,OnEditDone)
	MSG_WM_LBUTTONDOWN(OnLButtonClick)
		MSG_WM_LBUTTONDBLCLK(OnLButtonDClick)
		MSG_WM_TIMER(OnTimer)
		MSG_WM_SIZE(OnSize);
	MSG_WM_COMMAND(OnCommand);
	MSG_WM_VSCROLL(OnVScroll);
	MSG_WM_MOUSEWHEEL(OnMouseWheel);
	MSG_WM_PAINT(OnPaint)
		MSG_WM_SETFONT(OnSetFont)
		//	CHAIN_MSG_MAP(CDoubleBufferImpl<MemoryViewer>)
	END_MSG_MAP()
	DECLARE_WND_CLASS("PDP8MemoryViewer")
	std::vector<int> word_tabs = { 20, 30, 40, 50, 60, 70, 80, 90 ,100 };
	void OnSetFont(HFONT font, BOOL redraw) {
		CClientDC dc(*this);

		auto backup = dc.SelectFont(font);
		dc.GetTextMetricsA(&m_metric);
		m_charsize.cx = m_metric.tmAveCharWidth;
		m_charsize.cy = m_metric.tmHeight + m_metric.tmExternalLeading;
		m_cxcaps = (m_metric.tmPitchAndFamily & 1 ? 3 : 2) * m_charsize.cx / 2;
		
		word_tabs.clear();
		word_tabs.push_back(m_metric.tmMaxCharWidth *6 + (m_metric.tmMaxCharWidth/2));
		for(size_t i=0;i < m_words_per_line;i++)
			word_tabs.push_back(m_metric.tmMaxCharWidth * 5);
		dc.SelectFont(backup);
		m_edit.SetFont(font, redraw);
		
		SetMsgHandled(false);
	}
	LRESULT OnEditDone(UINT command, UINT id, HWND wnd, BOOL& handled/*bHandled*/)
	{
		//util::error("Sent!\n");
		unselect();
		handled = true;
		return 0;
	}
	TEXTMETRIC m_metric;
	CSize m_charsize;
	CSize m_window_size;
	int m_cxcaps;
	int iAccumDelta = 0;
	int iDeltaPerLine = 120;

	bool m_word_selected;
	bool m_word_selecting;
	int m_end_of_selection, m_current_word;
	int m_max_offset_len, m_word_space, m_scroll_pos;
	size_t m_hscroll_pos, m_vscroll_pos;
	size_t m_visable_line_count;

	size_t m_words_per_line = 8;
	size_t m_current_digit = 0;
	size_t m_line_space_above = 0;

	line_view_t m_line_view;
	CRect m_lines_offset;
	bool m_has_focus;
	word_info* m_selected = nullptr;
	int m_selected_pos = 0;
	CWordEdit m_edit;
	//CEdit m_edit;
	CFont m_font;
	CSize m_word_text_size;
	CSize m_client_size;
public:
	std::vector<uint16_t> test_memory;
	std::vector<uint8_t> test_attrib;
	std::unordered_set<size_t> m_changed;

	uint8_t* attribs = nullptr;
	uint16_t* memory = nullptr;
	size_t memory_size = 32768;
	
	size_t max_lines() const { return memory_size / m_words_per_line; }
	void OnCommand(UINT command, int id, HWND wnd) {
		if (id == ID_NEDIT) {
			switch (command) {

			};
		}
	}
	void unselect() {
		if (m_selected) {
			m_edit.ShowWindow(SW_HIDE);
			m_selected = nullptr;
			Invalidate();
		}
	}
	void select(word_info* info) {
		if (info != m_selected) {
			unselect();
			m_selected = info;
			m_edit.ShowText(&m_selected->rect, m_selected->address());
			set_caret_pos();
			//InvalidateRect(m_selected->rect);
			Invalidate();
		}
	}
	int PosToWord(CPoint pos) {
		CRect rect;
		rect.top = 0;
		rect.bottom = m_charsize.cy;
		for (size_t i = m_vscroll_pos; i < m_visable_line_count; i++) {
			rect.left = word_tabs[0];
			rect.right = m_client_size.cx;
			if (rect.PtInRect(pos)) {
				for (size_t j = 0; j < m_words_per_line; j++) {
					rect.left = word_tabs[0] -(m_metric.tmMaxCharWidth/2);
					rect.right = word_tabs[j + 1] + (m_metric.tmMaxCharWidth / 2);
					if (rect.PtInRect(pos)) return i * 8 + j;
				}
				break;
			}
			rect.top = rect.bottom;
			rect.bottom += m_charsize.cy;
		}
		return -1;
	}
	void OnLButtonClick(UINT wParm, CPoint pos) {
		auto word = PosToWord(pos);
		if (word != -1) {
			attribs[word] = DCA_SELECTED;
			Invalidate(true);
		}
	}
	void OnLButtonDClick(UINT wParm, CPoint pos) {
		auto m_test = m_selected;
		unselect();
		auto word = PosToWord(pos);
		if (word != -1) {
		//	select(word);
			SetMsgHandled(TRUE);
		}
		else
			SetMsgHandled(FALSE);
		Invalidate();
	}
	void set_caret_pos()
	{
		if (m_selected) {
			m_edit.MoveWindow(&m_selected->rect);
		}

	}

	LRESULT OnMouseWheel(UINT hParm, short delta, CPoint pos) {
		//	if (iDeltaPerLine == 0) break;

		iAccumDelta += delta;     // 120 or -120
		while (iAccumDelta >= iDeltaPerLine)
		{
			SendMessageA(WM_VSCROLL, SB_LINEUP);
			iAccumDelta -= iDeltaPerLine;
		}

		while (iAccumDelta <= -iDeltaPerLine)
		{
			SendMessageA(WM_VSCROLL, SB_LINEDOWN);
			iAccumDelta += iDeltaPerLine;
		}
		return 0;
	}

	void OnVScroll(int scroll_msg, short p2, HWND win) {
		SCROLLINFO si;
		si.cbSize = sizeof(si);
		si.fMask = SIF_ALL;
		GetScrollInfo(SB_VERT, &si);
		int iVertPos = si.nPos;
		switch (scroll_msg) {
		case SB_TOP: si.nPos = si.nMin; break;
		case SB_BOTTOM: si.nPos = si.nMax; break;
		case SB_LINEUP: si.nPos -= 1; break;
		case SB_LINEDOWN: si.nPos += 1; break;
		case SB_PAGEUP: si.nPos -= si.nPage; break;
		case SB_PAGEDOWN: si.nPos += si.nPage; break;
		case SB_THUMBTRACK: si.nPos = si.nTrackPos; break;
		default: break;
		}
		si.fMask = SIF_POS;
		SetScrollInfo(SB_VERT, &si, true);
		GetScrollInfo(SB_VERT, &si);
		// if the position changed update it
		if (si.nPos != iVertPos) {
			ScrollWindow(0, m_charsize.cy * (iVertPos - si.nPos));
			Invalidate();
			//RedrawWindow();
		}
	//	if(iVertPos>0)
		m_vscroll_pos = size_t(iVertPos);
		set_caret_pos();
	}
	void recaculate_rects() {
		int lines = m_client_size.cy / m_charsize.cy;
		m_text_rects.resize(lines);
		for (int i = 0; i < lines; i++) {
			auto& line = m_text_rects[i];
			line.resize(m_words_per_line);
			CPoint pos(m_charsize.cx * 6, i * m_charsize.cy);
			CSize size(m_charsize.cx * 6, m_charsize.cy);
			for (int j = 0; j < m_words_per_line; j++) {
				line[j] = CRect(pos, size);
				pos.x += size.cx;
			}
		}
	}
	void OnSize(UINT wParm, CSize size) {
		SCROLLINFO si;
		si.cbSize = sizeof(si);
		si.fMask = SIF_RANGE | SIF_PAGE;
		si.nMin = 0;
		si.nMax = max_lines() - 1;
		si.nPage = size.cy / m_charsize.cy;
		SetScrollInfo(SB_VERT, &si, true);
		m_lines_offset.left = size.cx - m_lines_offset.right - 1;
		m_lines_offset.bottom =  size.cy - m_lines_offset.top - 1;
		m_visable_line_count = (size.cy / m_charsize.cy);
		m_client_size = size;
		recaculate_rects();
	}
	LRESULT OnCreate(LPCREATESTRUCT lpcs) {
	
		m_vscroll_pos = 0;
		test_memory.resize(memory_size);
		test_attrib.resize(memory_size);
		memory = test_memory.data();
		attribs = test_attrib.data();
		m_line_view.set_data(test_memory.data(), test_memory.size());
		m_selected = nullptr;
		//m_edit.Create(*this);
		
		m_edit.Create(*this, 0, 0, WS_CHILD,0,(HMENU)ID_NEDIT);
		m_edit.SetMargins(0, 0);
		m_edit.ShowWindow(SW_HIDE);

		iAccumDelta = 0;

		m_font.CreateFontA(0, 0, 0, 0, 0, 0, 0, 0, DEFAULT_CHARSET, 0, 0, 0, FIXED_PITCH, NULL);
		SetFont(m_font, false);
		return 0;
	}
	void OnDestroy() {
		unselect();

		SetMsgHandled(true);
	}
	void OnTimer(UINT uTimerID)//, TIMERPROC pTimerProc)
	{
	}
	void ToOct(std::string& buffer, int value, int digits=4) {
		for(int shift=digits*3-3; shift >=0; shift-=3)
			buffer.push_back( '0' + ((value >> shift) & 0x7));
	}


	void OnPaint(HDC dcc) {
		std::string buffer;
		CPaintDC dc(*this);

		SCROLLINFO si;

		CRect rect;
		GetClientRect(&rect);
		si.cbSize = sizeof(si);
		si.fMask = SIF_POS;
		GetScrollInfo(SB_VERT, &si);

		int iVertPos = si.nPos;
		int iPaintBeg = max(0, iVertPos + dc.m_ps.rcPaint.top / m_charsize.cy);
		int iPaintEnd = min(max_lines() - 1, iVertPos + dc.m_ps.rcPaint.bottom / m_charsize.cy);
		int iHorzPos = 0;

		CPoint pos(0, 0);

		auto old_font = dc.SelectFont(m_font);
		uint8_t last_attrib=DCA_NORMAL;
		CBrush every_other_line;
		every_other_line.CreateSolidBrush(GetAttribColor(DCA_ANCILLARY).second);
		CBrush white_brush;
		white_brush.CreateSolidBrush(RGB(0xFF,0xFF,0xFF));
		CBrush cache_brush;
		cache_brush.CreateSolidBrush(RGB(0xFF, 0xFF, 0xFF));
		dc.SetBkMode(TRANSPARENT);
		dc.SetBkColor(RGB(0x00, 0x00, 0x00));
		CSize size;
		for (int i = iPaintBeg; i <= iPaintEnd; i++) {
			pos.x = 0;
			CRect rect(pos, CSize(m_client_size.cx,m_charsize.cy));
			dc.FillRect(rect, (i & 1) == 0 ? every_other_line : white_brush);
			buffer.clear();
			
			uint16_t offset = i * m_words_per_line;
			const uint16_t* address = memory + offset;
			ToOct(buffer, offset, 5);
			dc.ExtTextOut(pos.x, pos.y, 0, nullptr, buffer.c_str(), buffer.size());
			pos.x += word_tabs[0];
			for (int j = 0; j < m_words_per_line; j++) {
				buffer.clear();
				ToOct(buffer, memory[offset], 4);
				dc.GetTextExtent(buffer.data(), buffer.size(), &size);
				rect = CRect(pos, size);
				uint8_t attrib = attribs[offset];
				if (last_attrib != attrib) {
					auto colors = GetAttribColor(attrib);
					dc.SetTextColor(colors.first);
					if (!DCA_BACKGROUND_EQUAL(last_attrib, attrib)) {
						cache_brush.DeleteObject();
						cache_brush.CreateSolidBrush(colors.second);
					}
				}
				if (!DCA_BACKGROUND_EQUAL(DCA_NORMAL, attrib)) {
					dc.FillRect(&rect, cache_brush);
				}
				dc.ExtTextOut(pos.x, pos.y, ETO_OPAQUE, nullptr, buffer.c_str(), buffer.size()); 
				pos.x += word_tabs[j+1];
				offset++;
			}
			pos.y += m_charsize.cy;
		} 
		dc.SelectFont(old_font);
		SetMsgHandled(FALSE);
	}
};
