/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ public class ProcMemIOTs
/*   4:    */   implements Device, Constants
/*   5:    */ {
/*   6:    */   public static final int DevId00 = 0;
/*   7:    */   public static final int DevId16 = 14;
/*   8:    */   public static final int DevId17 = 15;
/*   9:    */   public static final int DevId20 = 16;
/*  10:    */   public static final int DevId21 = 17;
/*  11:    */   public static final int DevId22 = 18;
/*  12:    */   public static final int DevId23 = 19;
/*  13:    */   public static final int DevId24 = 20;
/*  14:    */   public static final int DevId25 = 21;
/*  15:    */   public static final int DevId26 = 22;
/*  16:    */   public static final int DevId27 = 23;
/*  17:    */   private BusRegMem data;
/*  18:    */   private int field;
/*  19:    */   private int dsr;
/*  20:    */   private int ibr;
/*  21:    */   private int isr;
/*  22:    */   private boolean ubr;
/*  23:    */   private boolean usr;
/*  24:    */   private boolean uint;
/*  25:    */   private int trapinst;
/*  26: 41 */   private int[] relmem = new int[8];
/*  27:    */   private long trapreg;
/*  28:    */   private int trapdev;
/*  29: 44 */   private static final byte[] iottable = { 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, 1, 1, 1, -1, -1, -1, -1, -1, 1, 1, 1, 0, -1, -1, -1, -1, 1, 1, 1, 0, -1, -1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, 1, 1, 1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
/*  30:    */   
/*  31:    */   public ProcMemIOTs(BusRegMem data)
/*  32:    */   {
/*  33: 65 */     this.data = data;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void Decode(int devcode, int opcode)
/*  37:    */   {
/*  38: 69 */     if (devcode == 0)
/*  39:    */     {
/*  40: 70 */       switch (opcode)
/*  41:    */       {
/*  42:    */       case 0: 
/*  43:    */         break;
/*  44:    */       case 1: 
/*  45:    */         break;
/*  46:    */       case 2: 
/*  47:    */         break;
/*  48:    */       case 3: 
/*  49:    */         break;
/*  50:    */       case 4: 
/*  51: 75 */         this.data.c0c1c2 = 3; break;
/*  52:    */       case 5: 
/*  53: 76 */         this.data.c0c1c2 = 0; break;
/*  54:    */       case 6: 
/*  55:    */         break;
/*  56:    */       }
/*  57:    */     }
/*  58: 80 */     else if (((devcode >= 16 ? 1 : 0) & (devcode <= 23 ? 1 : 0)) != 0)
/*  59:    */     {
/*  60: 81 */       this.field = (devcode & 0x7);
/*  61: 82 */       switch (opcode)
/*  62:    */       {
/*  63:    */       case 0: 
/*  64:    */         break;
/*  65:    */       case 1: 
/*  66: 84 */         this.data.c0c1c2 = 0; break;
/*  67:    */       case 2: 
/*  68: 85 */         this.data.c0c1c2 = 0; break;
/*  69:    */       case 3: 
/*  70: 86 */         this.data.c0c1c2 = 0; break;
/*  71:    */       case 4: 
/*  72: 87 */         switch (this.field)
/*  73:    */         {
/*  74:    */         case 0: 
/*  75:    */           break;
/*  76:    */         case 1: 
/*  77: 89 */           this.data.c0c1c2 = 2; break;
/*  78:    */         case 2: 
/*  79: 90 */           this.data.c0c1c2 = 2; break;
/*  80:    */         case 3: 
/*  81: 91 */           this.data.c0c1c2 = 2; break;
/*  82:    */         case 4: 
/*  83:    */           break;
/*  84:    */         case 5: 
/*  85:    */           break;
/*  86:    */         }
/*  87: 96 */         break;
/*  88:    */       case 5: 
/*  89: 97 */         switch (this.field)
/*  90:    */         {
/*  91:    */         case 0: 
/*  92: 98 */           this.data.c0c1c2 = 3; break;
/*  93:    */         case 1: 
/*  94:    */           break;
/*  95:    */         case 2: 
/*  96:    */           break;
/*  97:    */         case 3: 
/*  98:101 */           this.data.c0c1c2 = 1; break;
/*  99:    */         case 4: 
/* 100:102 */           this.data.c0c1c2 = 1;
/* 101:    */         }
/* 102:103 */         break;
/* 103:    */       }
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void Execute(int devcode, int opcode)
/* 108:    */   {
/* 109:111 */     if (devcode == 0)
/* 110:    */     {
/* 111:112 */       switch (opcode)
/* 112:    */       {
/* 113:    */       case 0: 
/* 114:113 */         this.data.skipbus = this.data.intena;this.data.setInterruptOff(); break;
/* 115:    */       case 1: 
/* 116:114 */         this.data.intena = true; break;
/* 117:    */       case 2: 
/* 118:115 */         this.data.setInterruptOff(); break;
/* 119:    */       case 3: 
/* 120:116 */         this.data.skipbus = (this.data.intreq > 0L); break;
/* 121:    */       case 4: 
/* 122:118 */         if (this.data.link > 0) {
/* 123:118 */           this.data.data |= 0x800;
/* 124:    */         }
/* 125:119 */         if (this.data.gtf > 0) {
/* 126:119 */           this.data.data |= 0x400;
/* 127:    */         }
/* 128:120 */         if (this.data.intreq > 0L) {
/* 129:120 */           this.data.data |= 0x200;
/* 130:    */         }
/* 131:122 */         if (this.data.intena) {
/* 132:122 */           this.data.data |= 0x80;
/* 133:    */         }
/* 134:123 */         if (this.usr) {
/* 135:123 */           this.data.data |= 0x40;
/* 136:    */         }
/* 137:124 */         this.data.data |= (this.isr << 3) + this.dsr;
/* 138:125 */         break;
/* 139:    */       case 5: 
/* 140:127 */         this.data.link = ((this.data.data & 0x800) >> 11);
/* 141:128 */         this.data.gtf = ((this.data.data & 0x400) >> 10);
/* 142:129 */         this.data.intinhibit = true;
/* 143:130 */         this.data.intena = true;
/* 144:131 */         this.ubr = ((this.data.data & 0x40) > 0);
/* 145:132 */         this.ibr = ((this.data.data & 0x38) >> 3);
/* 146:133 */         this.data.dfr = (this.data.data & 0x7);
/* 147:134 */         break;
/* 148:    */       case 6: 
/* 149:135 */         this.data.skipbus = (this.data.gtf > 0); break;
/* 150:    */       case 7: 
/* 151:136 */         this.data.ClearAllFlags();
/* 152:    */       }
/* 153:    */     }
/* 154:138 */     else if (((devcode >= 16 ? 1 : 0) & (devcode <= 23 ? 1 : 0)) != 0)
/* 155:    */     {
/* 156:139 */       this.field = (devcode & 0x7);
/* 157:140 */       switch (opcode)
/* 158:    */       {
/* 159:    */       case 0: 
/* 160:141 */         this.data.dfr = this.data.ifr; break;
/* 161:    */       case 1: 
/* 162:142 */         this.data.dfr = this.field; break;
/* 163:    */       case 2: 
/* 164:143 */         this.ibr = this.field;this.data.intinhibit = true; break;
/* 165:    */       case 3: 
/* 166:144 */         this.data.dfr = this.field;this.ibr = this.field;this.data.intinhibit = true; break;
/* 167:    */       case 4: 
/* 168:145 */         switch (this.field)
/* 169:    */         {
/* 170:    */         case 0: 
/* 171:146 */           this.uint = false;this.data.setIntReq(16, this.uint); break;
/* 172:    */         case 1: 
/* 173:147 */           this.data.data |= this.data.dfr << 3; break;
/* 174:    */         case 2: 
/* 175:148 */           this.data.data |= this.data.ifr << 3; break;
/* 176:    */         case 3: 
/* 177:149 */           this.data.data |= (this.isr << 3) + this.dsr;
/* 178:149 */           if (this.usr) {
/* 179:149 */             this.data.data |= 0x40;
/* 180:    */           }
/* 181:    */           break;
/* 182:    */         case 4: 
/* 183:150 */           this.ibr = this.isr;this.data.dfr = this.dsr;this.ubr = this.usr;this.data.intinhibit = true; break;
/* 184:    */         case 5: 
/* 185:151 */           this.data.skipbus = this.uint; break;
/* 186:    */         case 6: 
/* 187:152 */           this.ubr = false;this.uint = false;this.data.setIntReq(16, this.uint); break;
/* 188:    */         case 7: 
/* 189:153 */           this.ubr = true;this.data.intinhibit = true;
/* 190:    */         }
/* 191:154 */         break;
/* 192:    */       case 5: 
/* 193:155 */         switch (this.field)
/* 194:    */         {
/* 195:    */         case 0: 
/* 196:156 */           this.data.data = this.trapinst; break;
/* 197:    */         case 1: 
/* 198:157 */           this.data.skipbus = this.data.mmena; break;
/* 199:    */         case 2: 
/* 200:158 */           this.data.skipbus = true; break;
/* 201:    */         case 3: 
/* 202:160 */           this.trapdev = ((this.data.data & 0x1F8) >> 3);
/* 203:161 */           if ((this.data.data & 0x1) > 0) {
/* 204:162 */             this.trapreg |= 1 << this.trapdev;
/* 205:    */           } else {
/* 206:164 */             this.trapreg &= (1 << this.trapdev ^ 0xFFFFFFFF);
/* 207:    */           }
/* 208:166 */           break;
/* 209:    */         case 4: 
/* 210:167 */           this.relmem[(this.data.data & 0x7)] = ((this.data.data & 0x38) >> 3); break;
/* 211:    */         case 6: 
/* 212:168 */           this.data.mmena = true; break;
/* 213:    */         case 7: 
/* 214:169 */           this.data.mmena = false;
/* 215:    */         }
/* 216:    */         break;
/* 217:    */       }
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   public void Interrupt(int command)
/* 222:    */   {
/* 223:176 */     switch (command)
/* 224:    */     {
/* 225:    */     case 1: 
/* 226:178 */       this.isr = this.ibr;this.data.ifr = 0;this.ibr = 0;this.data.ema = 0;
/* 227:179 */       this.dsr = this.data.dfr;this.data.dfr = 0;
/* 228:180 */       this.usr = this.ubr;this.data.usermode = false;this.ubr = false;
/* 229:181 */       break;
/* 230:    */     case 2: 
/* 231:183 */       if (((this.data.state == 2 ? 1 : 0) & (this.data.ir != 5 ? 1 : 0) & (this.data.ir != 4 ? 1 : 0)) != 0) {
/* 232:184 */         if ((this.data.mmena & this.data.usermode)) {
/* 233:185 */           this.data.ema = (this.relmem[this.data.dfr] << 12);
/* 234:    */         } else {
/* 235:187 */           this.data.ema = (this.data.dfr << 12);
/* 236:    */         }
/* 237:    */       }
/* 238:190 */       if (((this.data.ir == 5 ? 1 : 0) | (this.data.ir == 4 ? 1 : 0)) != 0) {
/* 239:191 */         if (((this.data.state == 1 ? 1 : 0) & ((this.data.md & 0x100) == 0 ? 1 : 0) | (this.data.state == 2 ? 1 : 0)) != 0)
/* 240:    */         {
/* 241:192 */           this.data.ifr = this.ibr;
/* 242:193 */           this.data.usermode = this.ubr;
/* 243:194 */           this.data.intinhibit = false;
/* 244:    */         }
/* 245:    */       }
/* 246:    */       break;
/* 247:    */     case 3: 
/* 248:199 */       int hotst = this.data.md & 0xF07;
/* 249:200 */       if (((hotst == 3842 ? 1 : 0) | (hotst == 3844 ? 1 : 0) | (hotst == 3846 ? 1 : 0)) != 0) {
/* 250:200 */         setTrap();
/* 251:    */       }
/* 252:201 */       if (this.data.ir == 6) {
/* 253:202 */         if (this.data.mmena)
/* 254:    */         {
/* 255:203 */           this.trapdev = ((this.data.md & 0x1F8) >> 3);
/* 256:204 */           if (iottable[(this.data.md & 0x1FF)] < 0) {
/* 257:204 */             setTrap();
/* 258:205 */           } else if (((this.trapreg & 1 << this.trapdev) == 0L) && 
/* 259:206 */             (iottable[(this.data.md & 0x1FF)] != 0)) {
/* 260:206 */             setTrap();
/* 261:    */           }
/* 262:    */         }
/* 263:    */         else
/* 264:    */         {
/* 265:208 */           setTrap();
/* 266:    */         }
/* 267:    */       }
/* 268:210 */       break;
/* 269:    */     case 4: 
/* 270:212 */       if ((this.data.mmena & this.data.usermode)) {
/* 271:213 */         this.data.ema = (this.relmem[this.data.ifr] << 12);
/* 272:    */       } else {
/* 273:215 */         this.data.ema = (this.data.ifr << 12);
/* 274:    */       }
/* 275:217 */       break;
/* 276:    */     case 5: 
/* 277:219 */       if ((this.data.mmena & this.data.usermode)) {
/* 278:220 */         this.data.ema = (this.relmem[this.data.dfr] << 12);
/* 279:    */       } else {
/* 280:222 */         this.data.ema = (this.data.dfr << 12);
/* 281:    */       }
/* 282:    */       break;
/* 283:    */     }
/* 284:    */   }
/* 285:    */   
/* 286:    */   private void setTrap()
/* 287:    */   {
/* 288:229 */     this.trapinst = this.data.md;
/* 289:230 */     this.uint = true;
/* 290:231 */     this.data.setIntReq(16, this.uint);
/* 291:232 */     if (this.data.ir == 6)
/* 292:    */     {
/* 293:233 */       this.data.ir = 7;
/* 294:234 */       this.data.md = 3584;
/* 295:    */     }
/* 296:    */     else
/* 297:    */     {
/* 298:236 */       this.data.md &= 0xFF9;
/* 299:    */     }
/* 300:    */   }
/* 301:    */   
/* 302:    */   public void ClearFlags(int devcode)
/* 303:    */   {
/* 304:241 */     this.data.ac = 0;
/* 305:242 */     this.data.link = 0;
/* 306:243 */     this.uint = false;
/* 307:244 */     this.data.setIntReq(0, false);
/* 308:245 */     this.data.setIntReq(16, this.uint);
/* 309:246 */     this.data.setInterruptOff();
/* 310:    */   }
/* 311:    */   
/* 312:    */   public void ClearRun(boolean run) {}
/* 313:    */   
/* 314:    */   public void CloseDev(int devcode) {}
/* 315:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.ProcMemIOTs
 * JD-Core Version:    0.7.0.1
 */