#include "global.h"
#include "main_window.h"
#include "asembler_editor.h"

void debug_out(const char* fmt, ...) {
	char buffer[512];
	va_list va;
	va_start(va, fmt);
	vsprintf_s(buffer, fmt, va);
	va_end(va);
	::OutputDebugStringA(buffer);
}

CAppModule _Module;
int CALLBACK WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int  nCmdShow) {
	CMessageLoop messageLoop;
	//AppWindow mainwnd;
	MACRO8_Asembler mainwnd;
	_Module.Init(NULL, NULL);
	_Module.AddMessageLoop(&messageLoop);
	//	mainwnd.Create(NULL, CWindow::rcDefault, _T("Main Window"));
	// initialize a rect with size of window to create
	RECT rc = { 0, 0, 640, 480 };
	if (mainwnd.CreateEx(NULL, rc) == NULL)
	{
		ATLTRACE(_T("Main window creation failed!\n"));
		return 0;
	}

	// disable the maximize box
	mainwnd.ModifyStyle(WS_MAXIMIZEBOX, 0);

	// center main window in desktop area
	mainwnd.CenterWindow();
	mainwnd.ShowWindow(SW_SHOW);
	mainwnd.UpdateWindow();


	int nRet = messageLoop.Run();

	_Module.RemoveMessageLoop();
	_Module.Term();
	return nRet;
}