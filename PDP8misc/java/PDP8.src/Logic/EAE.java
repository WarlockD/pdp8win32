/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ public class EAE
/*   4:    */   implements Constants
/*   5:    */ {
/*   6:    */   BusRegMem data;
/*   7:    */   
/*   8:    */   public EAE(BusRegMem data)
/*   9:    */   {
/*  10: 17 */     this.data = data;
/*  11:    */   }
/*  12:    */   
/*  13:    */   public int Decode(int instr)
/*  14:    */   {
/*  15: 26 */     if (instr == 3865)
/*  16:    */     {
/*  17: 27 */       this.data.modeb = true;
/*  18: 28 */       return this.data.link << 12;
/*  19:    */     }
/*  20: 30 */     if (instr == 3879)
/*  21:    */     {
/*  22: 31 */       this.data.modeb = false;
/*  23: 32 */       this.data.gtf = 0;
/*  24: 33 */       return this.data.link << 12;
/*  25:    */     }
/*  26: 35 */     if (!this.data.modeb)
/*  27:    */     {
/*  28: 36 */       if ((instr & 0x20) != 0) {
/*  29: 36 */         this.data.data |= this.data.sc;
/*  30:    */       }
/*  31:    */       long longreg;
/*  32:    */       long longreg;
/*  33: 37 */       switch (instr >> 1 & 0x7)
/*  34:    */       {
/*  35:    */       case 1: 
/*  36: 39 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma + 1)];
/*  37: 40 */         this.data.sc = ((this.data.md ^ 0xFFFFFFFF) & 0x1F);
/*  38: 41 */         this.data.skip = true;
/*  39: 42 */         this.data.pdp8time += 14L;
/*  40: 43 */         break;
/*  41:    */       case 2: 
/*  42: 45 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma + 1)];
/*  43: 46 */         longreg = this.data.mq * this.data.md + this.data.data;
/*  44: 47 */         this.data.link = 0;
/*  45: 48 */         this.data.data = ((int)(longreg >> 12) & 0xFFF);
/*  46: 49 */         this.data.mq = ((int)longreg & 0xFFF);
/*  47: 50 */         this.data.sc = 12;
/*  48: 51 */         this.data.skip = true;
/*  49: 52 */         this.data.pdp8time += 62L;
/*  50: 53 */         break;
/*  51:    */       case 3: 
/*  52: 55 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma + 1)];
/*  53: 56 */         if (this.data.data - this.data.md < 0)
/*  54:    */         {
/*  55: 57 */           longreg = this.data.data << 12 | this.data.mq;
/*  56: 58 */           this.data.mq = ((int)(longreg / this.data.md) & 0xFFF);
/*  57: 59 */           this.data.data = ((int)(longreg - this.data.mq * this.data.md));
/*  58: 60 */           this.data.link = 0;
/*  59: 61 */           this.data.sc = 13;
/*  60:    */         }
/*  61:    */         else
/*  62:    */         {
/*  63: 63 */           this.data.mq = ((this.data.mq << 1) + 1 & 0xFFF);
/*  64:    */           
/*  65: 65 */           this.data.link = 1;
/*  66: 66 */           this.data.sc = 1;
/*  67:    */         }
/*  68: 68 */         this.data.skip = true;
/*  69: 69 */         this.data.pdp8time += 62L;
/*  70: 70 */         break;
/*  71:    */       case 4: 
/*  72: 72 */         longreg = getLong();
/*  73: 73 */         for (this.data.sc = 0; ((longreg & 0x1000000) == (longreg & 0x800000) << 1) && ((longreg & 0x7FFFFF) != 0L); this.data.sc += 1) {
/*  74: 75 */           longreg <<= 1;
/*  75:    */         }
/*  76: 76 */         setLong(longreg);
/*  77: 77 */         this.data.pdp8time += 3 * this.data.sc;
/*  78: 78 */         break;
/*  79:    */       case 5: 
/*  80: 80 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma + 1)];
/*  81: 81 */         this.data.sc = (this.data.md + 1 & 0x3F);
/*  82: 82 */         longreg = getLong();
/*  83: 83 */         longreg <<= this.data.sc;
/*  84: 84 */         setLong(longreg);
/*  85: 85 */         this.data.sc = 0;
/*  86: 86 */         this.data.skip = true;
/*  87: 87 */         this.data.pdp8time += 14 + 3 * this.data.sc;
/*  88: 88 */         break;
/*  89:    */       case 6: 
/*  90: 90 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma + 1)];
/*  91: 91 */         this.data.sc = (this.data.md + 1 & 0x3F);
/*  92: 92 */         this.data.link = 2;
/*  93: 93 */         longreg = getLong();
/*  94: 94 */         longreg >>= this.data.sc;
/*  95: 95 */         setLong(longreg);
/*  96: 96 */         this.data.sc = 0;
/*  97: 97 */         this.data.skip = true;
/*  98: 98 */         this.data.pdp8time += 14 + 3 * this.data.sc;
/*  99: 99 */         break;
/* 100:    */       case 7: 
/* 101:101 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma + 1)];
/* 102:102 */         this.data.sc = (this.data.md + 1 & 0x3F);
/* 103:103 */         this.data.link = 0;
/* 104:104 */         longreg = getLong();
/* 105:105 */         longreg >>>= this.data.sc;
/* 106:106 */         setLong(longreg);
/* 107:107 */         this.data.sc = 0;
/* 108:108 */         this.data.skip = true;
/* 109:109 */         this.data.pdp8time += 14 + 3 * this.data.sc;
/* 110:    */       }
/* 111:112 */       this.data.gtf = 0;
/* 112:    */     }
/* 113:    */     else
/* 114:    */     {
/* 115:    */       long longreg;
/* 116:    */       long longreg;
/* 117:114 */       switch (((instr & 0x20) >> 1) + (instr & 0xE) >> 1)
/* 118:    */       {
/* 119:    */       case 1: 
/* 120:116 */         this.data.sc = (this.data.data & 0x1F);
/* 121:117 */         this.data.data = 0;
/* 122:118 */         break;
/* 123:    */       case 2: 
/* 124:120 */         this.data.ma = chkAuto(this.data.ma + 1);
/* 125:    */         
/* 126:    */ 
/* 127:    */ 
/* 128:124 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma)];
/* 129:125 */         longreg = this.data.mq * this.data.md + this.data.data;
/* 130:126 */         this.data.link = 0;
/* 131:127 */         this.data.data = ((int)(longreg >> 12) & 0xFFF);
/* 132:128 */         this.data.mq = ((int)longreg & 0xFFF);
/* 133:129 */         this.data.sc = 12;
/* 134:130 */         this.data.skip = true;
/* 135:131 */         this.data.pdp8time += 62L;
/* 136:132 */         break;
/* 137:    */       case 3: 
/* 138:134 */         this.data.ma = chkAuto(this.data.ma + 1);
/* 139:    */         
/* 140:    */ 
/* 141:    */ 
/* 142:138 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma)];
/* 143:139 */         if (this.data.data - this.data.md < 0)
/* 144:    */         {
/* 145:140 */           longreg = this.data.data << 12 | this.data.mq;
/* 146:141 */           this.data.mq = ((int)(longreg / this.data.md) & 0xFFF);
/* 147:142 */           this.data.data = ((int)(longreg - this.data.mq * this.data.md));
/* 148:143 */           this.data.link = 0;
/* 149:144 */           this.data.sc = 13;
/* 150:    */         }
/* 151:    */         else
/* 152:    */         {
/* 153:146 */           this.data.mq = ((this.data.mq << 1) + 1 & 0xFFF);
/* 154:    */           
/* 155:148 */           this.data.link = 1;
/* 156:149 */           this.data.sc = 1;
/* 157:    */         }
/* 158:151 */         this.data.skip = true;
/* 159:152 */         this.data.pdp8time += 62L;
/* 160:153 */         break;
/* 161:    */       case 4: 
/* 162:155 */         longreg = this.data.link << 24 | this.data.data << 12 | this.data.mq;
/* 163:156 */         for (this.data.sc = 0; ((longreg & 0x800000) == (longreg & 0x400000) << 1) && ((longreg & 0x3FFFFF) != 0L); this.data.sc += 1) {
/* 164:158 */           longreg <<= 1;
/* 165:    */         }
/* 166:159 */         this.data.link = ((int)(longreg >> 24) & 0x1);
/* 167:160 */         this.data.data = ((int)(longreg >> 12) & 0xFFF);
/* 168:161 */         this.data.mq = ((int)longreg & 0xFFF);
/* 169:162 */         this.data.pdp8time += 3 * this.data.sc;
/* 170:163 */         break;
/* 171:    */       case 5: 
/* 172:165 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma + 1)];
/* 173:166 */         this.data.sc = (this.data.md & 0x1F);
/* 174:167 */         longreg = this.data.link << 25 | this.data.data << 13 | this.data.mq << 1;
/* 175:168 */         longreg <<= this.data.sc;
/* 176:169 */         this.data.link = ((int)(longreg >> 25) & 0x1);
/* 177:170 */         this.data.data = ((int)(longreg >> 13) & 0xFFF);
/* 178:171 */         this.data.mq = ((int)(longreg >> 1) & 0xFFF);
/* 179:172 */         this.data.sc = 31;
/* 180:173 */         this.data.skip = true;
/* 181:174 */         this.data.pdp8time += 17 + 3 * this.data.sc;
/* 182:175 */         break;
/* 183:    */       case 6: 
/* 184:177 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma + 1)];
/* 185:178 */         this.data.sc = (this.data.md & 0x1F);
/* 186:179 */         long tl = (this.data.data & 0x800) > 0 ? 549755813887L : 0L;
/* 187:180 */         longreg = tl << 25 | this.data.data << 13 | this.data.mq << 1 | this.data.gtf;
/* 188:181 */         longreg >>= this.data.sc;
/* 189:182 */         this.data.link = ((int)(longreg >> 25) & 0x1);
/* 190:183 */         this.data.data = ((int)(longreg >> 13) & 0xFFF);
/* 191:184 */         this.data.mq = ((int)(longreg >> 1) & 0xFFF);
/* 192:185 */         this.data.gtf = ((int)longreg & 0x1);
/* 193:186 */         this.data.sc = 31;
/* 194:187 */         this.data.skip = true;
/* 195:188 */         this.data.pdp8time += 17 + 3 * this.data.sc;
/* 196:189 */         break;
/* 197:    */       case 7: 
/* 198:191 */         this.data.md = this.data.memory[(this.data.ema | this.data.ma + 1)];
/* 199:192 */         this.data.sc = (this.data.md & 0x1F);
/* 200:193 */         this.data.link = 0;
/* 201:194 */         longreg = this.data.data << 13 | this.data.mq << 1 | this.data.gtf;
/* 202:195 */         longreg >>>= this.data.sc;
/* 203:196 */         this.data.data = ((int)(longreg >> 13) & 0xFFF);
/* 204:197 */         this.data.mq = ((int)(longreg >> 1) & 0xFFF);
/* 205:198 */         this.data.gtf = ((int)longreg & 0x1);
/* 206:199 */         this.data.sc = 31;
/* 207:200 */         this.data.skip = true;
/* 208:201 */         this.data.pdp8time += 17 + 3 * this.data.sc;
/* 209:202 */         break;
/* 210:    */       case 8: 
/* 211:204 */         this.data.data |= this.data.sc;
/* 212:205 */         break;
/* 213:    */       case 9: 
/* 214:207 */         this.data.ma = chkAuto(this.data.ma + 1);
/* 215:    */         
/* 216:    */ 
/* 217:    */ 
/* 218:211 */         long longreg1 = this.data.memory[(this.data.ema | this.data.ma + 1)] << 12 | this.data.memory[(this.data.ema | this.data.ma)];
/* 219:212 */         longreg = this.data.data << 12 | this.data.mq;
/* 220:213 */         longreg += longreg1;
/* 221:214 */         this.data.link = ((int)(longreg >> 24) & 0x1);
/* 222:215 */         this.data.data = ((int)(longreg >> 12) & 0xFFF);
/* 223:216 */         this.data.mq = ((int)longreg & 0xFFF);
/* 224:217 */         this.data.skip = true;
/* 225:218 */         this.data.pdp8time += 40L;
/* 226:219 */         break;
/* 227:    */       case 10: 
/* 228:221 */         this.data.ma = chkAuto(this.data.ma + 1);
/* 229:    */         
/* 230:    */ 
/* 231:    */ 
/* 232:225 */         this.data.memory[(this.data.ema | this.data.ma + 1)] = this.data.data;
/* 233:226 */         this.data.memory[(this.data.ema | this.data.ma)] = this.data.mq;
/* 234:227 */         this.data.skip = true;
/* 235:228 */         this.data.pdp8time += 40L;
/* 236:229 */         break;
/* 237:    */       case 11: 
/* 238:231 */         this.data.modeb = false;
/* 239:232 */         this.data.gtf = 0;
/* 240:233 */         break;
/* 241:    */       case 12: 
/* 242:235 */         if ((this.data.data | this.data.mq) == 0) {
/* 243:235 */           this.data.skip = true;
/* 244:    */         }
/* 245:    */         break;
/* 246:    */       case 13: 
/* 247:238 */         longreg = this.data.mq << 12 | this.data.data;
/* 248:239 */         longreg += 1L;
/* 249:240 */         this.data.link = ((int)(longreg >> 24) & 0x1);
/* 250:241 */         this.data.data = ((int)(longreg >> 12) & 0xFFF);
/* 251:242 */         this.data.mq = ((int)longreg & 0xFFF);
/* 252:243 */         this.data.pdp8time += 4L;
/* 253:244 */         break;
/* 254:    */       case 14: 
/* 255:246 */         longreg = this.data.mq << 12 | this.data.data;
/* 256:247 */         longreg = ((longreg ^ 0xFFFFFFFF) & 0xFFFFFF) + 1L;
/* 257:248 */         this.data.link = ((int)(longreg >> 24) & 0x1);
/* 258:249 */         this.data.data = ((int)(longreg >> 12) & 0xFFF);
/* 259:250 */         this.data.mq = ((int)longreg & 0xFFF);
/* 260:251 */         this.data.pdp8time += 4L;
/* 261:252 */         break;
/* 262:    */       case 15: 
/* 263:254 */         int temp = this.data.data;
/* 264:255 */         this.data.data = (this.data.mq + (temp ^ 0xFFF) + 1);
/* 265:256 */         boolean gtfb = (temp <= this.data.mq ? 1 : 0) ^ ((temp ^ this.data.mq) >> 11 > 0 ? 1 : 0);
/* 266:257 */         this.data.link = ((this.data.data & 0x1000) >> 12);
/* 267:258 */         this.data.data &= 0xFFF;
/* 268:259 */         this.data.gtf = (gtfb ? 1 : 0);
/* 269:    */       }
/* 270:    */     }
/* 271:263 */     return this.data.link << 12;
/* 272:    */   }
/* 273:    */   
/* 274:    */   private long getLong()
/* 275:    */   {
/* 276:267 */     long tl = this.data.link;
/* 277:268 */     if (this.data.link > 1) {
/* 278:268 */       tl = (this.data.data & 0x800) > 0 ? 549755813887L : 0L;
/* 279:    */     }
/* 280:269 */     return tl << 25 | this.data.data << 13 | this.data.mq << 1;
/* 281:    */   }
/* 282:    */   
/* 283:    */   private void setLong(long longreg)
/* 284:    */   {
/* 285:273 */     this.data.link = ((int)(longreg >> 25) & 0x1);
/* 286:274 */     this.data.data = ((int)(longreg >> 13) & 0xFFF);
/* 287:275 */     this.data.mq = ((int)(longreg >> 1) & 0xFFF);
/* 288:276 */     if (this.data.modeb) {
/* 289:276 */       this.data.gtf = ((int)longreg & 0x1);
/* 290:    */     }
/* 291:    */   }
/* 292:    */   
/* 293:    */   private int chkAuto(int marg)
/* 294:    */   {
/* 295:280 */     if ((marg & 0xFF8) == 8) {
/* 296:281 */       this.data.memory[(this.data.ema | marg)] = (this.data.memory[(this.data.ema | marg)] + 1 & 0xFFF);
/* 297:    */     }
/* 298:282 */     int darg = this.data.memory[(this.data.ema | marg)];
/* 299:283 */     this.data.devices[16];this.data.devices[16].Interrupt(5);
/* 300:284 */     return darg;
/* 301:    */   }
/* 302:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.EAE
 * JD-Core Version:    0.7.0.1
 */