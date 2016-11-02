#pragma once
#include "global.h"
#include <varargs.h>

// attribute bits for debug_view_char.attrib
constexpr uint8_t DCA_NORMAL = 0x00;     // black on white
constexpr uint8_t DCA_CHANGED = 0x01;     // red foreground
constexpr uint8_t DCA_SELECTED = 0x02;     // light red background
constexpr uint8_t DCA_INVALID = 0x04;     // dark blue foreground
constexpr uint8_t DCA_DISABLED = 0x08;     // darker foreground
constexpr uint8_t DCA_ANCILLARY = 0x10;     // grey background
constexpr uint8_t DCA_CURRENT = 0x20;     // yellow background
constexpr uint8_t DCA_COMMENT = 0x40;     // green foreground
constexpr uint8_t DCA_VISITED = 0x80;     // light blue background
constexpr uint8_t DCA_FOREGROUND = DCA_CHANGED | DCA_INVALID | DCA_DISABLED | DCA_COMMENT;
constexpr uint8_t DCA_BACKGROUND = DCA_SELECTED | DCA_ANCILLARY | DCA_CURRENT | DCA_VISITED;
constexpr uint8_t DCA_BACKGROUND_MASK = ~DCA_BACKGROUND; 
constexpr uint8_t DCA_FOREGROUND_MASK = ~DCA_FOREGROUND;

static inline bool DCA_BACKGROUND_EQUAL(uint8_t a1, uint8_t a2) { return (DCA_BACKGROUND_MASK & a1) == (DCA_BACKGROUND_MASK & a2); }
static inline bool DCA_FOREGROUND_EQUAL(uint8_t a1, uint8_t a2) { return (DCA_FOREGROUND_MASK & a1) == (DCA_FOREGROUND_MASK & a2); }

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
		dc_info(COLORREF default_bg= RGB(0xff, 0xff, 0xff)) : 
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
			//::GetTextExtentPoint32(hdc,text, tsize, &size);
			CRect rect = CRect(pos, size);//ETO_OPAQUE ETO_CLIPPED
			if (bg != RGB(0xFF, 0xFF, 0xFF)) {
				CBrush brush;

				brush.CreateSolidBrush(bg);
				dc.FillRect(&rect, brush);
				return rect;
			}
			CBrush brush;
		
			brush.CreateSolidBrush(bg);
			dc.SetBkColor(bg);
			dc.FillRect(&rect, brush);
			dc.SetTextColor(fg);
			dc.TextOutA(rect.left, rect.top, text, tsize);
			//dc.ExtTextOut(rect.left, rect.top, 0, nullptr, text, tsize, nullptr);
			//&rect																	 
			//::FillRect(hdc,&rect,)																 
			//::ExtTextOut(hdc,rect.left, rect.top, ETO_OPAQUE, &rect, text, tsize, nullptr);
			pos.x = rect.right;
			return rect;
		}
		CRect DrawString(HDC hdc, CPoint& pos, const char* text, size_t tsize) const {
			return DrawString(hdc, pos, text, tsize,fgcolor,bgcolor);
		}
	};
	struct word_info {
		int m_digit_base = 8;
		dc_info attrib;
		uint16_t* m_address;
		char text[16];
		CRect rect;
		word_info(uint16_t* address,int digit_base=8) : m_address(address), m_digit_base(digit_base) {
			int count = sprintf_s(text, "%4.4o", *m_address);
		}
		uint16_t* address() const { return m_address; }
		void address(uint16_t* addr) {
			if (addr != m_address) {
				m_address = addr;
				sprintf_s(text, "%4.4o", *m_address);
			}
		}
		void update_from_text() {
			long i = strtol(text, nullptr, m_digit_base);
			if (m_address) *m_address = i;
		}
		
		CRect draw(HDC hdc, CPoint& pos) {
			int value = m_address ? *m_address : 0;
			int count = sprintf_s(text, "%4.4o", value);
			return rect = attrib.DrawString(hdc, pos, text, count);
		}
	};
	struct line_t {
		size_t lineno = 0;
		size_t offset = 0;
		CRect rect;
		std::vector<word_info> line;

		CRect draw(HDC hdc,  CPoint& pos, int word_space=8) {
			CDCHandle dc(hdc);
			char buffer[64];
			int count = sprintf_s(buffer, "%5.5o : ", (uint16_t)offset);
			rect=dc_info::DrawString(hdc, pos, buffer, count, RGB(0x00, 0x00, 0x00), RGB(0xFF, 0xFF, 0xFF));
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
		line_view_t(size_t words_per_line=8) : m_data(nullptr), m_size(0) , m_words_per_line(words_per_line){ }
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
					}
				}
			}
		}
	};

	BEGIN_MSG_MAP(TerminalView)
		MSG_WM_CREATE(OnCreate)
		MSG_WM_DESTROY(OnDestroy)
		MSG_WM_SETFOCUS(OnSetFocus)
		MSG_WM_KILLFOCUS(OnKillFocus)
		MSG_WM_CHAR(OnChar);
		//MSG_WM_LBUTTONDBLCLK(OnLButtonClick)
		MSG_WM_LBUTTONDOWN(OnLButtonClick)
		MSG_WM_TIMER(OnTimer)
		MSG_WM_SIZE(OnSize);
	MSG_WM_VSCROLL(OnVScroll);
	MSG_WM_MOUSEWHEEL(OnMouseWheel);
	MSG_WM_PAINT(OnPaint)
		//	CHAIN_MSG_MAP(CDoubleBufferImpl<MemoryViewer>)
	END_MSG_MAP()
	DECLARE_WND_CLASS("PDP8MemoryViewer")

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
	CEdit m_edit;
public:
	std::vector<uint16_t> test_memory;
	const uint8_t* attribs = nullptr;
	const uint16_t* memory = nullptr;
	size_t memory_size = 32768;
	
	size_t max_lines() const { return memory_size / m_words_per_line; }
	void OnChar(TCHAR ch, UINT lparm, UINT rparm) {
		if (m_selected) {
			CRect rect = m_selected->rect;
			switch (ch) {
			case '\b': break;
			case '\r': // done unselect
				unselect();
				break;
			default:
				if (m_selected_pos >= 4) break;  
				if (ch >= '0' && ch <= '7') {
					m_selected->text[m_selected_pos++] = ch;
					m_selected->update_from_text();
					set_caret_pos();
				}
				break;
			}
			if (m_selected_pos > 4) m_selected_pos = 4;
			InvalidateRect(rect);
		}
	}
	void unselect() {
		if (m_selected) {
			//util::error("Unselected %5.5o\n", (size_t)m_selected->address());
			m_selected->attrib.flag_clear(DCA_SELECTED);
			m_selected->update_from_text();
			HideCaret();
			DestroyCaret();
			InvalidateRect(m_selected->rect);
			m_selected = nullptr;
		}
	}
	void select(word_info* info) {
		if (info != m_selected) {
			unselect();
			m_selected = info;
			m_selected->attrib.flag_set(DCA_SELECTED);
			//	util::error("Selected %5.5o\n", (size_t)m_selected->address());
			CreateSolidCaret(1, m_charsize.cy);
			m_selected_pos = 0;
			set_caret_pos();
			ShowCaret();
			InvalidateRect(m_selected->rect);
		}
	}
	void OnLButtonClick(UINT wParm, CPoint pos) {
		//if (m_has_focus) {
		auto m_test = m_selected;
		unselect();
		for (size_t i = m_vscroll_pos; i < m_visable_line_count; i++) {
			auto& l = m_line_view.m_lines[i];
			if (l.rect.PtInRect(pos)) {
				for (auto& ci : l.line) {
					if (ci.rect.PtInRect(pos)) {
						select(&ci);
						SetMsgHandled(TRUE);
						return;
					}
				}
				break;
			}
		}
		SetMsgHandled(FALSE);
		//	}
	}
	void set_caret_pos()
	{
		if (m_selected) {
			CDCHandle dc(GetDC());
			CSize size;
			dc.GetTextExtent(m_selected->text, m_selected_pos, &size);
			SetCaretPos(m_selected->rect.left + size.cx, m_selected->rect.top + 0* m_charsize.cy);
		}
	}
	void OnSetFocus(HWND hwnd) {
		//CreateSolidCaret(1, m_charsize.cy);
	//	ShowCaret();
		m_has_focus = true;
	//	set_caret_pos();
		//mark_char(0);
	}
	void OnKillFocus(HWND hwnd) {
		m_has_focus = false;
	//	HideCaret();
	//	DestroyCaret();

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
			RedrawWindow();
		}
	//	if(iVertPos>0)
		m_vscroll_pos = size_t(iVertPos);
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
	}
	LRESULT OnCreate(LPCREATESTRUCT lpcs) {
		m_vscroll_pos = 0;
		test_memory.resize(memory_size);
		memory = test_memory.data();
		m_line_view.set_data(test_memory.data(), test_memory.size());
		m_edit.Create(*this);
		m_edit.ShowWindow(SW_HIDE);

		CClientDC dc(*this);
		dc.GetTextMetricsA(&m_metric);
		m_charsize.cx = m_metric.tmAveCharWidth;
		m_charsize.cy = m_metric.tmHeight + m_metric.tmExternalLeading;
		m_cxcaps = (m_metric.tmPitchAndFamily & 1 ? 3 : 2) * m_charsize.cx / 2;
		iAccumDelta = 0;
		return 0;
	}
	void OnDestroy() {
	//	m_edit.ShowWindow(SW_HIDE);
	//	m_edit.DestroyWindow();

		SetMsgHandled(true);
	}
	void OnTimer(UINT uTimerID)//, TIMERPROC pTimerProc)
	{
	}

	void OnPaint(HDC dcc) {
		CPaintDC dc(*this);
		//SetBkMode(dc, TRANSPARENT);
		SCROLLINFO si;
		std::stringstream ss;
		CRect rect;
		GetClientRect(&rect);
		si.cbSize = sizeof(si);
		si.fMask = SIF_POS;
		GetScrollInfo(SB_VERT, &si);
		int iVertPos = si.nPos;
		int iPaintBeg = max(0, iVertPos + dc.m_ps.rcPaint.top / m_charsize.cy);
		int iPaintEnd = min(max_lines() - 1, iVertPos + dc.m_ps.rcPaint.bottom / m_charsize.cy);
		int iHorzPos = 0;

		std::string buffer;
		std::vector<char_attrib> viewdata;
		CPoint pos(0, 0);
		dc.SetBkMode(OPAQUE);
		for (int i = iPaintBeg; i <= iPaintEnd; i++) {
			auto& l = m_line_view.m_lines[i];
			l.draw(dc, pos);
			pos.y = l.rect.bottom;
			pos.x = l.rect.left;
		}
		if (m_selected) {
			auto old = dc.SelectStockBrush(NULL_BRUSH);
			auto old_p = dc.SelectStockPen(BLACK_PEN);
			dc.Rectangle(m_selected->rect);
			dc.SelectPen(old_p);
			dc.SelectBrush(old);
		}
	}
};
