/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ public class DK8EP
/*   4:    */   implements Device, Constants
/*   5:    */ {
/*   6: 17 */   public static int DevId13 = 11;
/*   7:    */   public BusRegMem data;
/*   8:    */   private VirTimer clktim;
/*   9:    */   private int clkreg;
/*  10:    */   private int clkbuf;
/*  11:    */   private int clkcnt;
/*  12:    */   private int clkstat;
/*  13:    */   private int clkmode;
/*  14:    */   private int clkrate;
/*  15:    */   private boolean clkflag;
/*  16:    */   private boolean clkinhib;
/*  17:    */   private boolean clkinten;
/*  18:    */   private boolean clkena;
/*  19:    */   
/*  20:    */   public DK8EP(BusRegMem data)
/*  21:    */   {
/*  22: 36 */     this.data = data;
/*  23:    */     
/*  24: 38 */     VirListener clk = new VirListener()
/*  25:    */     {
/*  26:    */       public void actionPerformed()
/*  27:    */       {
/*  28: 40 */         DK8EP.this.setCLK(true);
/*  29:    */       }
/*  30: 42 */     };
/*  31: 43 */     this.clktim = new VirTimer(data.virqueue, 10, clk);
/*  32: 44 */     this.clktim.setRepeats(true);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void Decode(int devcode, int opcode)
/*  36:    */   {
/*  37: 48 */     switch (opcode)
/*  38:    */     {
/*  39:    */     case 0: 
/*  40: 49 */       this.data.c0c1c2 = 0; break;
/*  41:    */     case 1: 
/*  42:    */       break;
/*  43:    */     case 2: 
/*  44: 51 */       this.data.c0c1c2 = 0; break;
/*  45:    */     case 3: 
/*  46: 52 */       this.data.c0c1c2 = 0; break;
/*  47:    */     case 4: 
/*  48: 53 */       this.data.c0c1c2 = 3; break;
/*  49:    */     case 5: 
/*  50: 54 */       this.data.c0c1c2 = 3; break;
/*  51:    */     case 6: 
/*  52: 55 */       this.data.c0c1c2 = 3; break;
/*  53:    */     case 7: 
/*  54: 56 */       this.data.c0c1c2 = 3;
/*  55:    */     }
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void Execute(int devcode, int opcode)
/*  59:    */   {
/*  60: 61 */     switch (opcode)
/*  61:    */     {
/*  62:    */     case 0: 
/*  63: 62 */       this.clkreg &= (this.data.data ^ 0xFFFFFFFF) & 0xFFF;setCommand(this.clkreg); break;
/*  64:    */     case 1: 
/*  65: 63 */       this.data.skipbus = this.clkflag; break;
/*  66:    */     case 2: 
/*  67: 64 */       this.clkreg |= this.data.data & 0xFFF;setCommand(this.clkreg); break;
/*  68:    */     case 3: 
/*  69: 65 */       this.clkbuf = (this.data.data & 0xFFF);this.clkcnt = this.clkbuf; break;
/*  70:    */     case 4: 
/*  71: 66 */       this.data.data = this.clkreg; break;
/*  72:    */     case 5: 
/*  73: 67 */       this.data.data = getStat(); break;
/*  74:    */     case 6: 
/*  75: 68 */       this.data.data = this.clkbuf; break;
/*  76:    */     case 7: 
/*  77: 69 */       this.clkbuf = this.clkcnt;this.data.data = this.clkbuf;
/*  78:    */     }
/*  79:    */   }
/*  80:    */   
/*  81:    */   private void setCommand(int ena)
/*  82:    */   {
/*  83: 75 */     this.clkena = ((ena & 0x800) != 0);
/*  84: 76 */     this.clkmode = ((ena & 0x600) >> 9);
/*  85: 77 */     this.clkrate = ((ena & 0x1C0) >> 6);
/*  86: 78 */     this.clktim.stop();
/*  87:    */     int delay;
/*  88: 79 */     switch (this.clkrate)
/*  89:    */     {
/*  90:    */     case 2: 
/*  91: 80 */       delay = 100000; break;
/*  92:    */     case 3: 
/*  93: 81 */       delay = 10000; break;
/*  94:    */     case 4: 
/*  95: 82 */       delay = 1000; break;
/*  96:    */     case 5: 
/*  97: 83 */       delay = 100; break;
/*  98:    */     case 6: 
/*  99: 84 */       delay = 10; break;
/* 100:    */     default: 
/* 101: 85 */       delay = 0;
/* 102:    */     }
/* 103: 87 */     if (delay > 0)
/* 104:    */     {
/* 105: 88 */       this.clktim.setInitialDelay(delay);
/* 106: 89 */       this.clktim.setDelay(delay);
/* 107: 90 */       this.clktim.start();
/* 108:    */     }
/* 109: 92 */     this.clkinhib = ((ena & 0x10) != 0);
/* 110: 93 */     this.clkinten = ((ena & 0x8) != 0);
/* 111:    */   }
/* 112:    */   
/* 113:    */   private int getStat()
/* 114:    */   {
/* 115: 97 */     if (this.clkena)
/* 116:    */     {
/* 117: 98 */       this.clkstat = (this.clkflag ? 2048 : 0);
/* 118: 99 */       setClkFlag(false);
/* 119:    */     }
/* 120:101 */     return this.clkstat;
/* 121:    */   }
/* 122:    */   
/* 123:    */   private void setCLK(boolean set)
/* 124:    */   {
/* 125:105 */     if (!this.clkinhib)
/* 126:    */     {
/* 127:106 */       this.clkcnt += 1;
/* 128:107 */       if (this.clkcnt > 4095)
/* 129:    */       {
/* 130:108 */         switch (this.clkmode)
/* 131:    */         {
/* 132:    */         case 0: 
/* 133:109 */           this.clkcnt = 0; break;
/* 134:    */         case 1: 
/* 135:110 */           this.clkcnt = this.clkbuf; break;
/* 136:    */         case 2: 
/* 137:111 */           this.clkcnt = 0; break;
/* 138:    */         case 3: 
/* 139:112 */           this.clkcnt = 0;
/* 140:    */         }
/* 141:114 */         setClkFlag(true);
/* 142:    */       }
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setClkFlag(boolean on)
/* 147:    */   {
/* 148:120 */     if (on == true)
/* 149:    */     {
/* 150:121 */       this.clkflag = true;
/* 151:122 */       if (this.clkena) {
/* 152:123 */         this.data.setIntReq(DevId13, this.clkinten);
/* 153:    */       }
/* 154:    */     }
/* 155:    */     else
/* 156:    */     {
/* 157:126 */       this.clkflag = false;
/* 158:127 */       this.data.setIntReq(DevId13, false);
/* 159:    */     }
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void ClearFlags(int devcode)
/* 163:    */   {
/* 164:132 */     this.clkreg = 0;
/* 165:133 */     this.clkbuf = 0;
/* 166:134 */     this.clkstat = 0;
/* 167:135 */     this.clkmode = 0;
/* 168:136 */     this.clkrate = 0;
/* 169:137 */     setClkFlag(false);
/* 170:138 */     this.clkinhib = false;
/* 171:139 */     this.clkinten = false;
/* 172:140 */     this.clkena = false;
/* 173:141 */     this.clktim.stop();
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void Interrupt(int command) {}
/* 177:    */   
/* 178:    */   public void ClearRun(boolean run) {}
/* 179:    */   
/* 180:    */   public void CloseDev(int devcode) {}
/* 181:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.DK8EP
 * JD-Core Version:    0.7.0.1
 */