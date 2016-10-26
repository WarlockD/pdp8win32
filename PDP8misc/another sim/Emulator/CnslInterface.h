#pragma once
#using <mscorlib.dll>

using namespace System;
using namespace System::Runtime::InteropServices;

namespace nsClearConsole
{
	public ref class ClearConsole
	{
	private:
		static int STD_OUTPUT_HANDLE  = -11;
		static unsigned char EMPTY = 32;

		[StructLayout(LayoutKind::Sequential)]
		value  struct COORD
		{
		public:
			short x;
			short y;
		};

		[StructLayout(LayoutKind::Sequential)]
		value  struct SMALL_RECT
		{
		public:
			short Left;
			short Top;
			short Right;
			short Bottom;
		};

		[StructLayout(LayoutKind::Sequential)]
		value  struct CONSOLE_SCREEN_BUFFER_INFO
		{
		public:
			COORD dwSize;
			COORD dwCursorPosition;
			int wAttributes;
			SMALL_RECT srWindow;
			COORD dwMaximumWindowSize;
		};


		[DllImport("kernel32.dll", EntryPoint="GetStdHandle", SetLastError=true, CharSet=CharSet::Auto, CallingConvention=CallingConvention::StdCall)]
		static int GetStdHandle(int nStdHandle);

		[DllImport("kernel32.dll", EntryPoint="FillConsoleOutputCharacter", SetLastError=true, CharSet=CharSet::Auto, CallingConvention=CallingConvention::StdCall)]
		static int FillConsoleOutputCharacter(int hConsoleOutput, Byte cCharacter, int nLength, COORD dwWriteCoord, int &lpNumberOfCharsWritten);

		[DllImport("kernel32.dll", EntryPoint="GetConsoleScreenBufferInfo", SetLastError=true, CharSet=CharSet::Auto, CallingConvention=CallingConvention::StdCall)]
		static int GetConsoleScreenBufferInfo(int hConsoleOutput, CONSOLE_SCREEN_BUFFER_INFO *lpConsoleScreenBufferInfo);

		[DllImport("kernel32.dll", EntryPoint="SetConsoleCursorPosition", SetLastError=true, CharSet=CharSet::Auto, CallingConvention=CallingConvention::StdCall)]
		static int SetConsoleCursorPosition(int hConsoleOutput, COORD dwCursorPosition);

	private:
		int hConsoleHandle;

	public:
		ClearConsole()
		{
			hConsoleHandle = GetStdHandle(STD_OUTPUT_HANDLE);
		}

	public:
		void Clear()
		{
			int hWrittenChars = 0;
			CONSOLE_SCREEN_BUFFER_INFO strConsoleInfo;
			COORD Home;

			GetConsoleScreenBufferInfo(hConsoleHandle, &strConsoleInfo);
			FillConsoleOutputCharacter(hConsoleHandle, EMPTY, strConsoleInfo.dwSize.x * strConsoleInfo.dwSize.y, Home, hWrittenChars);
			SetConsoleCursorPosition(hConsoleHandle, Home);
		}
	};
}
