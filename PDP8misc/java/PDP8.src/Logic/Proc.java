/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ public class Proc
/*   6:    */   extends Thread
/*   7:    */   implements Constants
/*   8:    */ {
/*   9:    */   public Proc(BusRegMem data, String name)
/*  10:    */   {
/*  11: 20 */     super(name);
/*  12: 21 */     this.data = data;
/*  13: 22 */     this.virqueue = data.virqueue;
/*  14:    */   }
/*  15:    */   
/*  16: 25 */   private boolean[] bit = new boolean[12];
/*  17:    */   public BusRegMem data;
/*  18:    */   public VirQueue virqueue;
/*  19:    */   private long starttime;
/*  20:    */   private long pdp8time;
/*  21:    */   private long pdp8inst;
/*  22: 31 */   private long avgadj = 0L;
/*  23:    */   private boolean current;
/*  24:    */   private boolean indirect;
/*  25:    */   private int lac;
/*  26:    */   private int md;
/*  27:    */   
/*  28:    */   public void run()
/*  29:    */   {
/*  30:    */     for (;;)
/*  31:    */     {
/*  32: 40 */       if (this.data.run)
/*  33:    */       {
/*  34: 42 */         synchronized (this.data)
/*  35:    */         {
/*  36: 43 */           if (this.data.msirdis)
/*  37:    */           {
/*  38: 44 */             Break();
/*  39:    */           }
/*  40:    */           else
/*  41:    */           {
/*  42: 46 */             if (((this.data.fset == 1 ? 1 : 0) & (this.data.cpma == 191 ? 1 : 0)) != 0) {
/*  43: 47 */               this.data.run = true;
/*  44:    */             }
/*  45: 49 */             if (this.data.intinprog) {
/*  46: 49 */               this.data.setInterruptOff();
/*  47:    */             }
/*  48: 51 */             if (this.data.fset == 1) {
/*  49: 52 */               if (this.data.devices[16] != null)
/*  50:    */               {
/*  51: 53 */                 this.data.devices[16];this.data.devices[16].Interrupt(4);
/*  52:    */               }
/*  53:    */               else
/*  54:    */               {
/*  55: 54 */                 this.data.ema = (this.data.ifr << 12);
/*  56:    */               }
/*  57:    */             }
/*  58: 56 */             this.data.state = this.data.fset;
/*  59: 57 */             switch (this.data.state)
/*  60:    */             {
/*  61:    */             case 1: 
/*  62: 58 */               Fetch(); break;
/*  63:    */             case 2: 
/*  64: 59 */               Defer(); break;
/*  65:    */             case 3: 
/*  66: 60 */               Execute();
/*  67:    */             }
/*  68:    */           }
/*  69: 63 */           if (((this.data.fset == 1 ? 1 : 0) & (this.data.halt == true ? 1 : 0)) != 0) {
/*  70: 64 */             this.data.run = false;
/*  71:    */           }
/*  72: 66 */           if (this.data.singstep == true) {
/*  73: 66 */             this.data.run = false;
/*  74:    */           }
/*  75: 68 */           this.data.pdp8inst += 1L;
/*  76: 69 */           float k = 1.0F;
/*  77: 70 */           for (long i = 0L; i < this.avgadj; i += 1L) {
/*  78: 71 */             k += k;
/*  79:    */           }
/*  80: 73 */           if (this.data.pdp8time - this.pdp8time >= 10000000L)
/*  81:    */           {
/*  82: 74 */             float elaps = (float)((System.currentTimeMillis() - this.starttime) * 1000L);
/*  83: 75 */             int adj = (int)((1000000.0D - elaps) / this.data.looptime / (this.data.pdp8inst - this.pdp8inst));
/*  84: 76 */             this.avgadj = (adj * 2 + this.avgadj);
/*  85: 76 */             if ((this.avgadj < 0L) || (this.data.speed == true)) {
/*  86: 76 */               this.avgadj = 0L;
/*  87:    */             }
/*  88: 78 */             this.starttime = System.currentTimeMillis();
/*  89: 79 */             this.pdp8time = this.data.pdp8time;this.pdp8inst = this.data.pdp8inst;
/*  90:    */           }
/*  91:    */         }
/*  92: 82 */         this.virqueue.postExpiredTimers();
/*  93:    */       }
/*  94:    */       else
/*  95:    */       {
/*  96:    */         try
/*  97:    */         {
/*  98: 85 */           Thread.sleep(20L);
/*  99:    */         }
/* 100:    */         catch (InterruptedException e)
/* 101:    */         {
/* 102: 86 */           System.out.println("Unknown error");
/* 103:    */         }
/* 104: 87 */         this.starttime = System.currentTimeMillis();
/* 105: 88 */         this.pdp8time = this.data.pdp8time;this.pdp8inst = this.data.pdp8inst;
/* 106: 89 */         this.data.ClearAllRun(this.data.run);
/* 107:    */       }
/* 108:    */     }
/* 109:    */   }
/* 110:    */   
/* 111:    */   private void Break()
/* 112:    */   {
/* 113: 94 */     if (this.data.keyctrl)
/* 114:    */     {
/* 115: 95 */       this.data.ma = this.data.cpma;
/* 116: 96 */       this.data.pc = (this.data.cpma + 1 & 0xFFF);
/* 117:    */     }
/* 118: 98 */     this.data.mb = (this.data.bkdctrl ? this.data.data + this.data.md & 0xFFF : this.data.data);
/* 119: 99 */     if (this.data.mddir) {
/* 120: 99 */       this.data.memory[(this.data.ema | this.data.ma)] = this.data.mb;
/* 121:    */     } else {
/* 122:100 */       this.data.md = this.data.memory[(this.data.ema | this.data.ma)];
/* 123:    */     }
/* 124:101 */     if (this.data.keyctrl) {
/* 125:101 */       this.data.run = false;
/* 126:    */     }
/* 127:102 */     if (!this.data.malctrl)
/* 128:    */     {
/* 129:104 */       this.data.cpma = (this.data.keyctrl ? this.data.pc : 0);
/* 130:105 */       this.data.ma = this.data.cpma;
/* 131:106 */       this.data.fset = 1;
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   private void Fetch()
/* 136:    */   {
/* 137:111 */     this.data.pdp8time += 12L;
/* 138:112 */     this.data.skip = false;
/* 139:113 */     this.data.ma = this.data.cpma;
/* 140:114 */     this.data.pc = (this.data.cpma + 1 & 0xFFF);
/* 141:115 */     if (this.data.intena) {
/* 142:115 */       this.data.intdelay = true;
/* 143:    */     }
/* 144:116 */     this.data.data = this.data.ac;
/* 145:117 */     this.lac = (this.data.ac + (this.data.link << 12));
/* 146:118 */     this.data.mddir = false;
/* 147:119 */     this.data.md = this.data.memory[(this.data.ema | this.data.ma)];
/* 148:    */     
/* 149:121 */     this.data.ir = (this.data.md >> 9);
/* 150:129 */     if ((this.data.usermode) && 
/* 151:130 */       (this.data.devices[16] != null))
/* 152:    */     {
/* 153:131 */       this.data.devices[16];this.data.devices[16].Interrupt(3);
/* 154:    */     }
/* 155:133 */     this.md = this.data.md;
/* 156:134 */     this.indirect = ((this.md & 0x100) != 0);
/* 157:135 */     this.current = ((this.md & 0x80) != 0);
/* 158:136 */     if (this.data.devices[16] != null)
/* 159:    */     {
/* 160:137 */       this.data.devices[16];this.data.devices[16].Interrupt(2);
/* 161:    */     }
/* 162:138 */     switch (this.data.ir)
/* 163:    */     {
/* 164:    */     case 0: 
/* 165:    */     case 1: 
/* 166:    */     case 2: 
/* 167:    */     case 3: 
/* 168:    */     case 4: 
/* 169:144 */       this.data.cpma = (this.md & 0x7F);
/* 170:145 */       if (this.current) {
/* 171:145 */         this.data.cpma += (this.data.ma & 0xF80);
/* 172:    */       }
/* 173:146 */       this.data.ma = this.data.cpma;
/* 174:147 */       if (this.indirect) {
/* 175:147 */         this.data.fset = 2;
/* 176:    */       } else {
/* 177:147 */         this.data.fset = 3;
/* 178:    */       }
/* 179:148 */       break;
/* 180:    */     case 5: 
/* 181:151 */       this.data.pc = (this.md & 0x7F);
/* 182:152 */       if (this.current) {
/* 183:152 */         this.data.pc += (this.data.ma & 0xF80);
/* 184:    */       }
/* 185:153 */       if (this.indirect)
/* 186:    */       {
/* 187:154 */         this.data.cpma = (this.md & 0x7F);
/* 188:155 */         if (this.current) {
/* 189:155 */           this.data.cpma += (this.data.ma & 0xF80);
/* 190:    */         }
/* 191:156 */         this.data.ma = this.data.cpma;
/* 192:157 */         this.data.fset = 2;
/* 193:    */       }
/* 194:    */       else
/* 195:    */       {
/* 196:158 */         CheckInt();
/* 197:    */       }
/* 198:159 */       break;
/* 199:    */     case 6: 
/* 200:162 */       this.data.iopause = true;
/* 201:163 */       int devcode = (this.md & 0x1F8) >> 3;
/* 202:164 */       int opcode = this.md & 0x7;
/* 203:165 */       if (this.data.devices[devcode] != null) {
/* 204:    */         do
/* 205:    */         {
/* 206:167 */           this.data.data = 0;
/* 207:168 */           this.data.c0c1c2 = 0;
/* 208:169 */           this.data.devices[devcode].Decode(devcode, opcode);
/* 209:170 */           if (((this.data.c0c1c2 == 0 ? 1 : 0) | (this.data.c0c1c2 == 1 ? 1 : 0) | (this.data.c0c1c2 == 2 ? 1 : 0)) != 0) {
/* 210:170 */             this.data.data = this.data.ac;
/* 211:    */           }
/* 212:171 */           this.data.devices[devcode].Execute(devcode, opcode);
/* 213:172 */           this.data.skip = this.data.skipbus;this.data.skipbus = false;
/* 214:173 */           if (this.data.c0c1c2 == 1) {
/* 215:173 */             this.data.ac = 0;
/* 216:    */           }
/* 217:174 */           if (((this.data.c0c1c2 == 2 ? 1 : 0) | (this.data.c0c1c2 == 3 ? 1 : 0)) != 0) {
/* 218:174 */             this.data.ac = this.data.data;
/* 219:    */           }
/* 220:175 */           if (this.data.c0c1c2 == 5) {
/* 221:175 */             this.data.pc = (this.data.pc + this.data.data & 0xFFF);
/* 222:    */           }
/* 223:176 */           if (this.data.c0c1c2 == 7) {
/* 224:176 */             this.data.pc = this.data.data;
/* 225:    */           }
/* 226:177 */           if (this.data.busstrobe) {
/* 227:177 */             this.data.lastxfer = true;
/* 228:    */           }
/* 229:178 */           this.data.busstrobe = false;
/* 230:179 */           this.data.iopause = false;
/* 231:180 */         } while (!this.data.lastxfer);
/* 232:    */       } else {
/* 233:182 */         System.out.println("Illegal IOT: " + Integer.toOctalString(this.md));
/* 234:    */       }
/* 235:184 */       CheckInt();
/* 236:185 */       break;
/* 237:    */     case 7: 
/* 238:188 */       if ((this.md & 0x100) == 0)
/* 239:    */       {
/* 240:189 */         if ((this.md & 0x80) != 0) {
/* 241:189 */           this.lac &= 0x1000;
/* 242:    */         }
/* 243:190 */         if ((this.md & 0x40) != 0) {
/* 244:190 */           this.lac &= 0xFFF;
/* 245:    */         }
/* 246:191 */         if ((this.md & 0x20) != 0) {
/* 247:191 */           this.lac ^= 0xFFF;
/* 248:    */         }
/* 249:192 */         if ((this.md & 0x10) != 0) {
/* 250:192 */           this.lac ^= 0x1000;
/* 251:    */         }
/* 252:193 */         if ((this.md & 0x1) != 0) {
/* 253:193 */           this.lac = (this.lac + 1 & 0x1FFF);
/* 254:    */         }
/* 255:194 */         if ((this.md & 0x2) != 0)
/* 256:    */         {
/* 257:195 */           if ((this.md & 0x4) != 0)
/* 258:    */           {
/* 259:196 */             if ((this.md & 0x8) != 0)
/* 260:    */             {
/* 261:197 */               this.lac = ((this.lac & 0x1000) + (this.data.md & 0x7F));
/* 262:197 */               if (this.current) {
/* 263:197 */                 this.lac += (this.data.ma & 0xF80);
/* 264:    */               }
/* 265:    */             }
/* 266:    */             else
/* 267:    */             {
/* 268:198 */               this.lac = ((this.lac << 2 | this.lac >> 11) & 0x1FFF);
/* 269:    */             }
/* 270:    */           }
/* 271:200 */           else if ((this.md & 0x8) != 0) {
/* 272:201 */             this.lac = ((this.lac >> 2 | this.lac << 11) & 0x1FFF);
/* 273:    */           } else {
/* 274:202 */             this.lac = (this.lac & 0x1000 | this.lac >> 6 & 0x3F | (this.lac & 0x3F) << 6);
/* 275:    */           }
/* 276:    */         }
/* 277:205 */         else if ((this.md & 0x4) != 0)
/* 278:    */         {
/* 279:206 */           if ((this.md & 0x8) != 0) {
/* 280:207 */             this.lac = (this.lac & 0x1000 | this.lac & this.data.md & 0xFFF);
/* 281:    */           } else {
/* 282:208 */             this.lac = ((this.lac << 1 | this.lac >> 12) & 0x1FFF);
/* 283:    */           }
/* 284:    */         }
/* 285:210 */         else if ((this.md & 0x8) != 0) {
/* 286:211 */           this.lac = ((this.lac >> 1 | this.lac << 12) & 0x1FFF);
/* 287:    */         }
/* 288:215 */         this.data.data = (this.lac & 0xFFF);
/* 289:    */       }
/* 290:217 */       else if ((this.md & 0x1) == 0)
/* 291:    */       {
/* 292:218 */         if (((this.md & 0x40) != 0) && ((this.lac & 0x800) > 0)) {
/* 293:218 */           this.data.skip = true;
/* 294:    */         }
/* 295:219 */         if (((this.md & 0x20) != 0) && ((this.lac & 0xFFF) == 0)) {
/* 296:219 */           this.data.skip = true;
/* 297:    */         }
/* 298:220 */         if (((this.md & 0x10) != 0) && ((this.lac & 0x1000) > 0)) {
/* 299:220 */           this.data.skip = true;
/* 300:    */         }
/* 301:221 */         if ((this.md & 0x8) != 0) {
/* 302:221 */           this.data.skip = (!this.data.skip);
/* 303:    */         }
/* 304:222 */         if ((this.md & 0x80) != 0) {
/* 305:222 */           this.lac &= 0x1000;
/* 306:    */         }
/* 307:223 */         if ((this.md & 0x4) != 0) {
/* 308:223 */           this.lac |= this.data.swr;
/* 309:    */         }
/* 310:224 */         if ((this.md & 0x2) != 0) {
/* 311:225 */           this.data.run = false;
/* 312:    */         }
/* 313:227 */         this.data.data = (this.lac & 0xFFF);
/* 314:    */       }
/* 315:    */       else
/* 316:    */       {
/* 317:229 */         this.data.data = 0;
/* 318:230 */         if ((((this.md & 0x80) == 0 ? 1 : 0) & ((this.md & 0x10) == 0 ? 1 : 0)) != 0) {
/* 319:230 */           this.data.data = (this.lac & 0xFFF);
/* 320:    */         }
/* 321:231 */         if ((this.md & 0x40) != 0) {
/* 322:231 */           this.data.data |= this.data.mq;
/* 323:    */         }
/* 324:232 */         if ((((this.md & 0x80) == 0 ? 1 : 0) & ((this.md & 0x10) != 0 ? 1 : 0)) != 0) {
/* 325:232 */           this.data.mq = (this.lac & 0xFFF);
/* 326:    */         }
/* 327:233 */         if ((((this.md & 0x80) != 0 ? 1 : 0) & ((this.md & 0x10) != 0 ? 1 : 0)) != 0) {
/* 328:233 */           this.data.mq = 0;
/* 329:    */         }
/* 330:234 */         if ((this.md & 0x2E) != 0) {
/* 331:234 */           this.lac = this.data.eae.Decode(this.md);
/* 332:    */         }
/* 333:235 */         this.lac = ((this.lac & 0x1000) + this.data.data);
/* 334:    */       }
/* 335:238 */       this.data.ac = (this.lac & 0xFFF);
/* 336:239 */       this.data.link = ((this.lac & 0x1000) >> 12);
/* 337:240 */       CheckInt();
/* 338:    */     }
/* 339:    */   }
/* 340:    */   
/* 341:    */   private void Defer()
/* 342:    */   {
/* 343:246 */     this.data.pdp8time += 12L;
/* 344:247 */     this.data.ma = this.data.cpma;
/* 345:248 */     this.data.mddir = false;
/* 346:249 */     this.data.md = this.data.memory[(this.data.ema | this.data.ma)];
/* 347:250 */     this.data.mb = (this.data.md + 1 & 0xFFF);
/* 348:251 */     if ((this.data.ma & 0xFF8) == 8)
/* 349:    */     {
/* 350:252 */       this.data.pdp8time += 2L;
/* 351:253 */       this.data.md = this.data.mb;
/* 352:254 */       this.data.mddir = true;
/* 353:255 */       this.data.memory[(this.data.ema | this.data.ma)] = this.data.md;
/* 354:    */     }
/* 355:257 */     if (this.data.ir == 5)
/* 356:    */     {
/* 357:258 */       this.data.pc = this.data.md;
/* 358:259 */       if (this.data.devices[16] != null)
/* 359:    */       {
/* 360:260 */         this.data.devices[16];this.data.devices[16].Interrupt(2);
/* 361:    */       }
/* 362:261 */       CheckInt();
/* 363:    */     }
/* 364:    */     else
/* 365:    */     {
/* 366:263 */       if (this.data.devices[16] != null)
/* 367:    */       {
/* 368:264 */         this.data.devices[16];this.data.devices[16].Interrupt(2);
/* 369:    */       }
/* 370:265 */       this.data.cpma = this.data.md;
/* 371:266 */       this.data.ma = this.data.cpma;
/* 372:267 */       this.data.fset = 3;
/* 373:    */     }
/* 374:    */   }
/* 375:    */   
/* 376:    */   private void Execute()
/* 377:    */   {
/* 378:277 */     this.data.pdp8time += 14L;
/* 379:278 */     this.data.ma = this.data.cpma;
/* 380:279 */     this.data.mddir = false;
/* 381:280 */     this.data.md = this.data.memory[(this.data.ema | this.data.ma)];
/* 382:281 */     this.data.mb = this.data.md;
/* 383:282 */     switch (this.data.ir)
/* 384:    */     {
/* 385:    */     case 0: 
/* 386:284 */       this.data.ac &= this.data.mb;
/* 387:285 */       break;
/* 388:    */     case 1: 
/* 389:288 */       this.lac = (this.data.ac + (this.data.link << 12) + this.data.mb);
/* 390:289 */       this.data.ac = (this.lac & 0xFFF);
/* 391:290 */       this.data.link = ((this.lac & 0x1000) >> 12);
/* 392:291 */       break;
/* 393:    */     case 2: 
/* 394:294 */       this.data.mb = (this.data.md + 1);
/* 395:295 */       if ((this.data.mb & 0x1000) > 0) {
/* 396:296 */         this.data.skip = true;
/* 397:    */       }
/* 398:298 */       this.data.mb &= 0xFFF;
/* 399:299 */       this.data.mddir = true;
/* 400:300 */       this.data.memory[(this.data.ema | this.data.ma)] = this.data.mb;
/* 401:301 */       break;
/* 402:    */     case 3: 
/* 403:304 */       this.data.data = this.data.ac;
/* 404:305 */       this.data.mb = this.data.data;
/* 405:306 */       this.data.mddir = true;
/* 406:307 */       this.data.memory[(this.data.ema | this.data.ma)] = this.data.mb;
/* 407:308 */       this.data.ac = 0;
/* 408:309 */       break;
/* 409:    */     case 4: 
/* 410:312 */       if (this.data.skip)
/* 411:    */       {
/* 412:313 */         this.data.mb = (this.data.pc + 1 & 0xFFF);
/* 413:314 */         this.data.skip = false;
/* 414:    */       }
/* 415:    */       else
/* 416:    */       {
/* 417:315 */         this.data.mb = this.data.pc;
/* 418:    */       }
/* 419:316 */       this.data.mddir = true;
/* 420:317 */       if (this.data.devices[16] != null)
/* 421:    */       {
/* 422:318 */         this.data.devices[16];this.data.devices[16].Interrupt(4);
/* 423:    */       }
/* 424:    */       else
/* 425:    */       {
/* 426:319 */         this.data.ema = (this.data.ifr << 12);
/* 427:    */       }
/* 428:320 */       this.data.memory[(this.data.ema | this.data.ma)] = this.data.mb;
/* 429:321 */       this.data.pc = (this.data.ma + 1 & 0xFFF);
/* 430:    */     }
/* 431:325 */     CheckInt();
/* 432:    */   }
/* 433:    */   
/* 434:    */   private void CheckInt()
/* 435:    */   {
/* 436:329 */     if ((!this.data.intinhibit & this.data.intdelay & this.data.intreq > 0L)) {
/* 437:329 */       this.data.intinprog = true;
/* 438:    */     }
/* 439:330 */     if (this.data.intinprog)
/* 440:    */     {
/* 441:331 */       this.data.ir = 4;
/* 442:332 */       this.data.cpma = 0;
/* 443:333 */       this.data.ma = this.data.cpma;
/* 444:334 */       if (this.data.devices[16] != null)
/* 445:    */       {
/* 446:335 */         this.data.devices[16];this.data.devices[16].Interrupt(1);
/* 447:    */       }
/* 448:336 */       this.data.fset = 3;
/* 449:    */     }
/* 450:    */     else
/* 451:    */     {
/* 452:338 */       this.data.cpma = this.data.pc;
/* 453:339 */       if (this.data.skip) {
/* 454:339 */         this.data.cpma = (this.data.pc + 1 & 0xFFF);
/* 455:    */       }
/* 456:340 */       this.data.ma = this.data.cpma;
/* 457:341 */       this.data.fset = 1;
/* 458:    */     }
/* 459:    */   }
/* 460:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.Proc
 * JD-Core Version:    0.7.0.1
 */