/*   1:    */ package Devices;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ 
/*   7:    */ public class VT100
/*   8:    */   extends Emulator
/*   9:    */ {
/*  10: 19 */   private int term_width = 82;
/*  11: 20 */   private int term_height = 24;
/*  12: 22 */   private int x = 0;
/*  13: 23 */   private int y = 0;
/*  14:    */   private int char_width;
/*  15:    */   private int char_height;
/*  16: 28 */   private Color fground = null;
/*  17: 29 */   private Color bground = null;
/*  18: 30 */   private Color bfground = null;
/*  19: 31 */   private Color bbground = null;
/*  20:    */   private byte b;
/*  21:    */   private char c;
/*  22:    */   private String arch;
/*  23: 37 */   private int region_y1 = 1;
/*  24: 38 */   private int region_y2 = this.term_height;
/*  25: 40 */   private int[] intarg = new int[16];
/*  26: 41 */   private int intargi = 0;
/*  27: 42 */   private boolean gotdigits = false;
/*  28: 44 */   private boolean on_off = false;
/*  29: 45 */   private boolean ignore = false;
/*  30: 46 */   private boolean punch_flag = false;
/*  31: 47 */   private boolean loop_flag = false;
/*  32: 49 */   private boolean cursor_appl = false;
/*  33: 50 */   private boolean col132 = false;
/*  34: 51 */   private boolean smooth = false;
/*  35: 52 */   private boolean reverse = false;
/*  36: 53 */   private boolean org_rel = false;
/*  37: 54 */   private boolean wrap = false;
/*  38: 55 */   private boolean repeat = false;
/*  39: 56 */   private boolean interlace = false;
/*  40: 57 */   private boolean keypad = false;
/*  41: 58 */   private boolean ansi = false;
/*  42: 59 */   private boolean newline = false;
/*  43: 60 */   private int char_attr = 0;
/*  44: 61 */   private int char_set = 0;
/*  45: 62 */   private boolean g0_graph = false;
/*  46: 63 */   private boolean g1_graph = false;
/*  47:    */   private boolean[] tab;
/*  48: 67 */   private char[] vt100_graphics = { '@', '♦', '▒', '␉', '␌', '␍', '␊', '°', '±', '␤', '␋', '┘', '┐', '┌', '└', '┼', '▔', '▀', '─', '▄', '▁', '├', '┤', '┴', '┬', '│', '≤', '≥', 'Π', '≠', '£', '·' };
/*  49:    */   
/*  50:    */   public VT100(Term term, InputStream in)
/*  51:    */   {
/*  52: 96 */     super(term, in);
/*  53: 97 */     this.term = term;
/*  54: 98 */     this.bfground = term.getFGround();
/*  55: 99 */     this.bbground = term.getBGround();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setPunch()
/*  59:    */   {
/*  60:103 */     this.punch_flag = true;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setLoopback(boolean loop)
/*  64:    */   {
/*  65:107 */     this.loop_flag = loop;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void Start()
/*  69:    */   {
/*  70:112 */     this.term_width = this.term.getColumnCount();
/*  71:113 */     this.term_height = this.term.getRowCount();
/*  72:    */     
/*  73:115 */     this.char_width = this.term.getCharWidth();
/*  74:116 */     this.char_height = this.term.getCharHeight();
/*  75:    */     
/*  76:118 */     int rx = 0;
/*  77:119 */     int ry = 0;
/*  78:120 */     int w = 0;
/*  79:121 */     int h = 0;
/*  80:    */     
/*  81:123 */     this.x = 0;
/*  82:124 */     this.y = this.char_height;
/*  83:    */     try
/*  84:    */     {
/*  85:    */       for (;;)
/*  86:    */       {
/*  87:129 */         this.b = getChar();
/*  88:    */         
/*  89:    */ 
/*  90:132 */         this.c = ((char)(this.b & 0x7F));
/*  91:134 */         if (this.loop_flag)
/*  92:    */         {
/*  93:135 */           int[] loo = new int[1];
/*  94:136 */           loo[0] = this.b;
/*  95:137 */           this.term.sendKeySeq(loo);
/*  96:    */         }
/*  97:140 */         else if (this.punch_flag)
/*  98:    */         {
/*  99:141 */           int intbyte = this.b & 127 + (this.b < 0 ? 128 : 0);
/* 100:    */           
/* 101:143 */           this.punch_flag = this.term.sendOutByte(intbyte);
/* 102:    */         }
/* 103:    */         else
/* 104:    */         {
/* 105:147 */           this.b = ((byte)(this.b & 0x7F));
/* 106:148 */           this.char_width = this.term.getCharWidth();
/* 107:149 */           this.char_height = this.term.getCharHeight();
/* 108:150 */           ry = this.y;
/* 109:151 */           rx = this.x;
/* 110:153 */           if (this.b != 0) {
/* 111:157 */             if (this.b == 27)
/* 112:    */             {
/* 113:158 */               this.b = getChar();
/* 114:160 */               if (this.b == 92)
/* 115:    */               {
/* 116:161 */                 this.ignore = false;
/* 117:    */               }
/* 118:165 */               else if (this.b == 60)
/* 119:    */               {
/* 120:166 */                 this.ansi = true;
/* 121:    */               }
/* 122:169 */               else if (this.b == 62)
/* 123:    */               {
/* 124:170 */                 this.keypad = false;
/* 125:    */               }
/* 126:173 */               else if (this.b == 61)
/* 127:    */               {
/* 128:174 */                 this.keypad = true;
/* 129:    */               }
/* 130:177 */               else if (this.b == 72)
/* 131:    */               {
/* 132:178 */                 this.tab[(this.x / this.char_width)] = true;
/* 133:    */               }
/* 134:181 */               else if (this.b == 80)
/* 135:    */               {
/* 136:182 */                 this.ignore = true;
/* 137:    */               }
/* 138:185 */               else if (this.b == 35)
/* 139:    */               {
/* 140:186 */                 this.b = getChar();
/* 141:187 */                 this.term.draw_cursor();
/* 142:188 */                 if (this.b == 54) {
/* 143:188 */                   this.term.setFont(32);
/* 144:    */                 }
/* 145:189 */                 if (this.b == 51) {
/* 146:189 */                   this.term.setFont(64);
/* 147:    */                 }
/* 148:190 */                 if (this.b == 52) {
/* 149:190 */                   this.term.setFont(128);
/* 150:    */                 }
/* 151:191 */                 if (this.b == 53) {
/* 152:191 */                   this.term.setFont(16);
/* 153:    */                 }
/* 154:192 */                 this.term.setBGround(this.term.getBGround());
/* 155:193 */                 this.term.draw_cursor();
/* 156:    */               }
/* 157:196 */               else if (this.b == 40)
/* 158:    */               {
/* 159:197 */                 this.b = getChar();
/* 160:198 */                 switch (this.b)
/* 161:    */                 {
/* 162:    */                 case 65: 
/* 163:    */                 case 66: 
/* 164:200 */                   this.g0_graph = false; break;
/* 165:    */                 case 48: 
/* 166:    */                 case 49: 
/* 167:    */                 case 50: 
/* 168:203 */                   this.g0_graph = true;
/* 169:    */                 }
/* 170:    */               }
/* 171:207 */               else if (this.b == 41)
/* 172:    */               {
/* 173:208 */                 this.b = getChar();
/* 174:209 */                 switch (this.b)
/* 175:    */                 {
/* 176:    */                 case 65: 
/* 177:    */                 case 66: 
/* 178:211 */                   this.g1_graph = false; break;
/* 179:    */                 case 48: 
/* 180:    */                 case 49: 
/* 181:    */                 case 50: 
/* 182:214 */                   this.g1_graph = true;
/* 183:    */                 }
/* 184:    */               }
/* 185:218 */               else if (this.b == 77)
/* 186:    */               {
/* 187:219 */                 this.term.draw_cursor();
/* 188:220 */                 this.term.scroll_window((this.region_y1 - 1) * this.char_height, (this.region_y2 - 1) * this.char_height, this.char_height, this.bground, this.smooth);
/* 189:221 */                 this.term.draw_cursor();
/* 190:    */               }
/* 191:224 */               else if (this.b == 68)
/* 192:    */               {
/* 193:225 */                 this.term.draw_cursor();
/* 194:226 */                 this.term.scroll_window(this.region_y1 * this.char_height, this.region_y2 * this.char_height, -this.char_height, this.bground, this.smooth);
/* 195:227 */                 this.term.draw_cursor();
/* 196:    */               }
/* 197:231 */               else if (this.b != 91)
/* 198:    */               {
/* 199:232 */                 err("ESC", this.b);
/* 200:233 */                 pushChar(this.b);
/* 201:    */               }
/* 202:    */               else
/* 203:    */               {
/* 204:237 */                 getDigits();
/* 205:238 */                 this.b = getChar();
/* 206:240 */                 if (this.b == 65)
/* 207:    */                 {
/* 208:241 */                   this.term.draw_cursor();
/* 209:242 */                   if (!this.gotdigits) {
/* 210:242 */                     this.intarg[0] = 1;
/* 211:    */                   }
/* 212:243 */                   this.y -= this.intarg[0] * this.char_height;
/* 213:244 */                   if (this.y <= 0) {
/* 214:244 */                     this.y = this.char_height;
/* 215:    */                   }
/* 216:245 */                   this.term.setCursor(this.x, this.y);
/* 217:246 */                   this.term.draw_cursor();
/* 218:    */                 }
/* 219:249 */                 else if (this.b == 66)
/* 220:    */                 {
/* 221:250 */                   this.term.draw_cursor();
/* 222:251 */                   if (!this.gotdigits) {
/* 223:251 */                     this.intarg[0] = 1;
/* 224:    */                   }
/* 225:252 */                   this.y += this.intarg[0] * this.char_height;
/* 226:253 */                   if (this.y > this.term_height * this.char_height) {
/* 227:253 */                     this.y = (this.term_height * this.char_height);
/* 228:    */                   }
/* 229:254 */                   this.term.setCursor(this.x, this.y);
/* 230:255 */                   this.term.draw_cursor();
/* 231:    */                 }
/* 232:258 */                 else if (this.b == 67)
/* 233:    */                 {
/* 234:259 */                   this.term.draw_cursor();
/* 235:260 */                   if (!this.gotdigits) {
/* 236:260 */                     this.intarg[0] = 1;
/* 237:    */                   }
/* 238:261 */                   this.x += this.intarg[0] * this.char_width;
/* 239:262 */                   if (this.x >= this.term_width * this.char_width) {
/* 240:262 */                     this.x = ((this.term_width - 1) * this.char_width);
/* 241:    */                   }
/* 242:263 */                   this.term.setCursor(this.x, this.y);
/* 243:264 */                   this.term.draw_cursor();
/* 244:    */                 }
/* 245:267 */                 else if (this.b == 68)
/* 246:    */                 {
/* 247:268 */                   this.term.draw_cursor();
/* 248:269 */                   if (!this.gotdigits) {
/* 249:269 */                     this.intarg[0] = 1;
/* 250:    */                   }
/* 251:270 */                   this.x -= this.intarg[0] * this.char_width;
/* 252:271 */                   if (this.x < 0) {
/* 253:271 */                     this.x = 0;
/* 254:    */                   }
/* 255:272 */                   this.term.setCursor(this.x, this.y);
/* 256:273 */                   this.term.draw_cursor();
/* 257:    */                 }
/* 258:276 */                 else if (((this.b == 72 ? 1 : 0) | (this.b == 102 ? 1 : 0)) != 0)
/* 259:    */                 {
/* 260:277 */                   this.term.draw_cursor();
/* 261:278 */                   if (!this.gotdigits)
/* 262:    */                   {
/* 263:278 */                     int tmp1351_1350 = 1;this.intarg[1] = tmp1351_1350;this.intarg[0] = tmp1351_1350;
/* 264:    */                   }
/* 265:279 */                   if (this.intarg[0] == 0) {
/* 266:279 */                     this.intarg[0] = 1;
/* 267:    */                   }
/* 268:280 */                   if (this.intarg[1] == 0) {
/* 269:280 */                     this.intarg[1] = 1;
/* 270:    */                   }
/* 271:281 */                   this.x = ((this.intarg[1] - 1) * this.char_width);
/* 272:282 */                   this.y = (this.intarg[0] * this.char_height);
/* 273:283 */                   this.term.setCursor(this.x, this.y);
/* 274:284 */                   this.term.draw_cursor();
/* 275:    */                 }
/* 276:287 */                 else if (this.b == 74)
/* 277:    */                 {
/* 278:288 */                   int ystart = 0;int yend = this.term_height * this.char_height;
/* 279:289 */                   if (this.intarg[0] == 0) {
/* 280:289 */                     ystart = this.y - this.char_height;
/* 281:    */                   }
/* 282:290 */                   if (this.intarg[0] == 1) {
/* 283:290 */                     yend = this.y;
/* 284:    */                   }
/* 285:291 */                   this.term.draw_cursor();
/* 286:292 */                   this.term.clear_area(0, ystart, this.term_width * this.char_width, yend);
/* 287:293 */                   this.term.redraw(0, ystart - this.char_height, this.term_width * this.char_width, yend - ystart + this.char_height);
/* 288:    */                   
/* 289:295 */                   this.term.draw_cursor();
/* 290:    */                 }
/* 291:298 */                 else if (this.b == 75)
/* 292:    */                 {
/* 293:299 */                   int xstart = 0;int xend = this.term_width * this.char_width;
/* 294:300 */                   if (this.intarg[0] == 0) {
/* 295:300 */                     xstart = this.x;
/* 296:    */                   }
/* 297:301 */                   if (this.intarg[0] == 1) {
/* 298:301 */                     xend = this.x + this.char_width;
/* 299:    */                   }
/* 300:302 */                   this.term.draw_cursor();
/* 301:303 */                   this.term.clear_area(xstart, this.y - this.char_height, xend, this.y);
/* 302:304 */                   this.term.redraw(xstart, this.y - this.char_height, xend - xstart, this.char_height);
/* 303:305 */                   this.term.draw_cursor();
/* 304:    */                 }
/* 305:309 */                 else if (this.b == 82)
/* 306:    */                 {
/* 307:310 */                   System.out.println("VT100-Cursor Position-Wrong direction");
/* 308:    */                 }
/* 309:314 */                 else if (this.b == 99)
/* 310:    */                 {
/* 311:315 */                   reset();
/* 312:    */                 }
/* 313:319 */                 else if (this.b == 103)
/* 314:    */                 {
/* 315:320 */                   if (this.intarg[0] == 0) {
/* 316:320 */                     this.tab[(this.x / this.char_width)] = false;
/* 317:    */                   } else {
/* 318:322 */                     for (this.x = 0; this.x < this.term_width; this.x += 1) {
/* 319:323 */                       this.tab[this.x] = false;
/* 320:    */                     }
/* 321:    */                   }
/* 322:    */                 }
/* 323:329 */                 else if (((this.b == 104 ? 1 : 0) | (this.b == 108 ? 1 : 0)) != 0)
/* 324:    */                 {
/* 325:332 */                   if (this.b == 104) {
/* 326:332 */                     this.on_off = true;
/* 327:    */                   } else {
/* 328:332 */                     this.on_off = false;
/* 329:    */                   }
/* 330:333 */                   this.term.draw_cursor();
/* 331:334 */                   switch (this.intarg[0])
/* 332:    */                   {
/* 333:    */                   case -1: 
/* 334:335 */                     this.cursor_appl = this.on_off; break;
/* 335:    */                   case -2: 
/* 336:336 */                     this.ansi = this.on_off; break;
/* 337:    */                   case -3: 
/* 338:337 */                     this.col132 = this.on_off; break;
/* 339:    */                   case -4: 
/* 340:338 */                     this.smooth = this.on_off; break;
/* 341:    */                   case -5: 
/* 342:339 */                     this.reverse = this.on_off; break;
/* 343:    */                   case -6: 
/* 344:340 */                     this.org_rel = this.on_off; break;
/* 345:    */                   case -7: 
/* 346:341 */                     this.wrap = this.on_off; break;
/* 347:    */                   case -8: 
/* 348:342 */                     this.repeat = this.on_off; break;
/* 349:    */                   case -9: 
/* 350:343 */                     this.interlace = this.on_off; break;
/* 351:    */                   case 20: 
/* 352:344 */                     this.newline = this.on_off;
/* 353:    */                   }
/* 354:346 */                   if (this.reverse)
/* 355:    */                   {
/* 356:347 */                     this.fground = this.bbground;
/* 357:348 */                     this.bground = this.bfground;
/* 358:    */                   }
/* 359:    */                   else
/* 360:    */                   {
/* 361:350 */                     this.fground = this.bfground;
/* 362:351 */                     this.bground = this.bbground;
/* 363:    */                   }
/* 364:353 */                   this.term.setFGround(this.fground);
/* 365:354 */                   this.term.setBGround(this.bground);
/* 366:355 */                   this.term.clear_area(0, 0, this.term_width * this.char_width, this.term_height * this.char_height);
/* 367:356 */                   this.term.redraw(0, 0, this.term_width * this.char_width, this.term_height * this.char_height);
/* 368:357 */                   this.term.draw_cursor();
/* 369:    */                 }
/* 370:361 */                 else if (this.b == 109)
/* 371:    */                 {
/* 372:362 */                   for (int i = 0; i <= this.intargi; i++) {
/* 373:363 */                     if (this.intarg[i] == 0)
/* 374:    */                     {
/* 375:364 */                       this.term.draw_cursor();
/* 376:365 */                       this.term.setFont(0);
/* 377:366 */                       this.term.draw_cursor();
/* 378:367 */                       this.term.setFGround(this.fground);
/* 379:368 */                       this.term.setBGround(this.bground);
/* 380:    */                     }
/* 381:371 */                     else if (this.intarg[i] == 1)
/* 382:    */                     {
/* 383:372 */                       this.term.setFont(1);
/* 384:    */                     }
/* 385:375 */                     else if (this.intarg[i] == 4)
/* 386:    */                     {
/* 387:376 */                       this.term.setFont(2);
/* 388:    */                     }
/* 389:379 */                     else if (this.intarg[i] == 5)
/* 390:    */                     {
/* 391:380 */                       this.term.setFGround(Color.cyan);
/* 392:    */                     }
/* 393:384 */                     else if (this.intarg[i] == 7)
/* 394:    */                     {
/* 395:385 */                       this.term.setFont(8);
/* 396:386 */                       this.term.setFGround(this.bground);
/* 397:387 */                       this.term.setBGround(this.fground);
/* 398:    */                     }
/* 399:    */                   }
/* 400:    */                 }
/* 401:394 */                 else if (this.b == 110)
/* 402:    */                 {
/* 403:395 */                   if (this.intarg[0] == 0) {
/* 404:395 */                     System.out.println("VT100-Ready-Wrong direction");
/* 405:    */                   }
/* 406:396 */                   if (this.intarg[0] == 5) {
/* 407:396 */                     this.term.sendKeySeq(getDSR());
/* 408:    */                   }
/* 409:397 */                   if (this.intarg[0] == 6)
/* 410:    */                   {
/* 411:398 */                     String seq = "\033[";
/* 412:399 */                     seq = seq + String.valueOf(this.x / this.char_width + 1) + ";" + String.valueOf(this.y / this.char_height) + "R";
/* 413:    */                   }
/* 414:    */                 }
/* 415:405 */                 else if (this.b == 114)
/* 416:    */                 {
/* 417:406 */                   if (!this.gotdigits)
/* 418:    */                   {
/* 419:406 */                     this.intarg[0] = 1;this.intarg[1] = this.term_height;
/* 420:    */                   }
/* 421:407 */                   if (this.intarg[1] == 0) {
/* 422:407 */                     this.intarg[1] = this.term_height;
/* 423:    */                   }
/* 424:408 */                   if (this.intarg[1] <= this.intarg[0]) {
/* 425:408 */                     this.intarg[1] = (this.intarg[0] + 1);
/* 426:    */                   }
/* 427:409 */                   this.region_y1 = this.intarg[0];
/* 428:410 */                   this.region_y2 = this.intarg[1];
/* 429:    */                 }
/* 430:413 */                 else if (this.b == 113)
/* 431:    */                 {
/* 432:414 */                   if (!this.gotdigits) {
/* 433:414 */                     this.intarg[0] = 0;
/* 434:    */                   }
/* 435:415 */                   for (int i = 0; i <= this.intargi; i++)
/* 436:    */                   {
/* 437:416 */                     this.term.setLEDs(this.intarg[i]);
/* 438:    */                     try
/* 439:    */                     {
/* 440:417 */                       Thread.sleep(100L);
/* 441:    */                     }
/* 442:    */                     catch (Exception e) {}
/* 443:    */                   }
/* 444:    */                 }
/* 445:    */                 else
/* 446:    */                 {
/* 447:423 */                   err("ESC [", this.b);
/* 448:    */                 }
/* 449:    */               }
/* 450:    */             }
/* 451:427 */             else if ((!this.ignore) && 
/* 452:    */             
/* 453:    */ 
/* 454:    */ 
/* 455:431 */               (this.b != 3))
/* 456:    */             {
/* 457:435 */               if (this.b == 7)
/* 458:    */               {
/* 459:436 */                 this.term.beep();
/* 460:    */               }
/* 461:440 */               else if (this.b == 8)
/* 462:    */               {
/* 463:441 */                 this.term.draw_cursor();
/* 464:442 */                 this.x -= this.char_width;
/* 465:443 */                 if (this.x < 0)
/* 466:    */                 {
/* 467:444 */                   this.y -= this.char_height;
/* 468:445 */                   if (this.y > 0)
/* 469:    */                   {
/* 470:445 */                     this.x = ((this.term_width - 1) * this.char_width);
/* 471:    */                   }
/* 472:    */                   else
/* 473:    */                   {
/* 474:446 */                     this.x = 0;this.y = this.char_height;
/* 475:    */                   }
/* 476:    */                 }
/* 477:448 */                 this.term.setCursor(this.x, this.y);
/* 478:449 */                 this.term.draw_cursor();
/* 479:    */               }
/* 480:453 */               else if (this.b == 9)
/* 481:    */               {
/* 482:    */                 try
/* 483:    */                 {
/* 484:456 */                   for (int t = this.x / this.char_width + 1; (t < this.term_width) && (this.tab[t] != 1); t++) {}
/* 485:457 */                   this.x = (t * this.char_width);
/* 486:    */                 }
/* 487:    */                 catch (Exception e)
/* 488:    */                 {
/* 489:458 */                   System.out.println(e);
/* 490:    */                 }
/* 491:460 */                 if (this.x >= this.term_width * this.char_width)
/* 492:    */                 {
/* 493:461 */                   this.x = 0;
/* 494:462 */                   this.y += this.char_height;
/* 495:463 */                   if (this.y > this.region_y2 * this.char_height) {
/* 496:463 */                     addLine();
/* 497:    */                   }
/* 498:    */                 }
/* 499:465 */                 this.term.draw_cursor();
/* 500:466 */                 this.term.setCursor(this.x, this.y);
/* 501:467 */                 this.term.draw_cursor();
/* 502:    */               }
/* 503:471 */               else if (this.b == 14)
/* 504:    */               {
/* 505:472 */                 this.char_set = 1;
/* 506:    */               }
/* 507:475 */               else if (this.b == 15)
/* 508:    */               {
/* 509:476 */                 this.char_set = 0;
/* 510:    */               }
/* 511:480 */               else if (this.b == 13)
/* 512:    */               {
/* 513:481 */                 this.term.draw_cursor();
/* 514:482 */                 this.term.setFont(16);
/* 515:483 */                 this.x = 0;
/* 516:484 */                 this.term.setCursor(this.x, this.y);
/* 517:485 */                 this.term.draw_cursor();
/* 518:    */               }
/* 519:    */               else
/* 520:    */               {
/* 521:490 */                 if (((this.b != 10 ? 1 : 0) & (this.b != 11 ? 1 : 0) & (this.b != 12 ? 1 : 0)) != 0)
/* 522:    */                 {
/* 523:491 */                   if (((this.b >= 0 ? 1 : 0) & (this.b < 31 ? 1 : 0)) != 0) {
/* 524:491 */                     err("CTL", this.b);
/* 525:    */                   }
/* 526:492 */                   if (this.x >= this.term_width * this.char_width)
/* 527:    */                   {
/* 528:493 */                     this.x = 0;
/* 529:494 */                     this.y += this.char_height;
/* 530:495 */                     if (this.y > this.region_y2 * this.char_height) {
/* 531:495 */                       addLine();
/* 532:    */                     }
/* 533:496 */                     rx = this.x;
/* 534:497 */                     ry = this.y;
/* 535:    */                   }
/* 536:500 */                   this.term.draw_cursor();
/* 537:502 */                   if ((this.b & 0x80) != 0)
/* 538:    */                   {
/* 539:503 */                     this.term.clear_area(this.x, this.y - this.char_height, this.x + this.char_width * 2, this.y);
/* 540:504 */                     byte[] foo = new byte[2];
/* 541:505 */                     foo[0] = this.b;
/* 542:506 */                     foo[1] = getChar();
/* 543:507 */                     this.term.drawString(new String(foo, 0, 2, "EUC-JP"), this.x, this.y);
/* 544:508 */                     this.x += this.char_width;
/* 545:509 */                     this.x += this.char_width;
/* 546:510 */                     w = this.char_width * 2;
/* 547:511 */                     h = this.char_height;
/* 548:    */                   }
/* 549:    */                   else
/* 550:    */                   {
/* 551:514 */                     this.term.clear_area(this.x, this.y - this.char_height, this.x + this.char_width, this.y);
/* 552:515 */                     char[] b2 = new char[1];
/* 553:518 */                     if ((((95 <= this.b) && (this.b <= 126)) & (this.char_set == 0 & this.g0_graph | this.char_set == 1 & this.g1_graph))) {
/* 554:519 */                       b2[0] = this.vt100_graphics[(this.b - 95)];
/* 555:    */                     } else {
/* 556:520 */                       b2[0] = this.c;
/* 557:    */                     }
/* 558:521 */                     this.term.drawChars(b2, 0, 1, this.x, this.y);
/* 559:522 */                     rx = this.x;
/* 560:523 */                     ry = this.y;
/* 561:524 */                     this.x += this.char_width;
/* 562:525 */                     w = this.char_width;
/* 563:526 */                     h = this.char_height;
/* 564:    */                   }
/* 565:528 */                   this.term.redraw(rx, ry - this.char_height, w, h);
/* 566:529 */                   this.term.setCursor(this.x, this.y);
/* 567:530 */                   this.term.draw_cursor();
/* 568:    */                 }
/* 569:    */                 else
/* 570:    */                 {
/* 571:532 */                   this.term.draw_cursor();
/* 572:533 */                   if (this.b != 12)
/* 573:    */                   {
/* 574:542 */                     if (this.newline == true)
/* 575:    */                     {
/* 576:543 */                       this.term.clear_area(this.x, this.y - this.char_height, this.x + this.char_width, this.y);
/* 577:544 */                       this.x = 0;
/* 578:    */                     }
/* 579:546 */                     this.y += this.char_height;
/* 580:    */                   }
/* 581:548 */                   this.term.setCursor(this.x, this.y);
/* 582:549 */                   this.term.draw_cursor();
/* 583:    */                 }
/* 584:553 */                 if (this.y == (this.region_y2 + 1) * this.char_height) {
/* 585:553 */                   addLine();
/* 586:    */                 }
/* 587:    */               }
/* 588:    */             }
/* 589:    */           }
/* 590:    */         }
/* 591:    */       }
/* 592:    */     }
/* 593:    */     catch (Exception e) {}
/* 594:    */   }
/* 595:    */   
/* 596:    */   private void err(String what, byte b)
/* 597:    */   {
/* 598:560 */     System.out.println(what + ": " + (char)b + ":" + Integer.toHexString(b & 0xFF));
/* 599:    */   }
/* 600:    */   
/* 601:    */   private void getDigits()
/* 602:    */   {
/* 603:564 */     this.intargi = 0;
/* 604:565 */     this.intarg[0] = 0;
/* 605:566 */     this.gotdigits = false;
/* 606:567 */     boolean qm = false;
/* 607:    */     for (;;)
/* 608:    */     {
/* 609:    */       try
/* 610:    */       {
/* 611:570 */         this.b = getChar();
/* 612:571 */         if (this.b == 63)
/* 613:    */         {
/* 614:572 */           qm = true;
/* 615:    */         }
/* 616:575 */         else if (this.b == 59)
/* 617:    */         {
/* 618:576 */           if (qm) {
/* 619:576 */             this.intarg[this.intargi] = (-this.intarg[this.intargi]);
/* 620:    */           }
/* 621:577 */           qm = false;
/* 622:578 */           this.intargi += 1;
/* 623:579 */           this.intarg[this.intargi] = 0;
/* 624:    */         }
/* 625:582 */         else if ((48 <= this.b) && (this.b <= 57))
/* 626:    */         {
/* 627:583 */           this.intarg[this.intargi] = (this.intarg[this.intargi] * 10 + (this.b - 48));
/* 628:584 */           this.gotdigits = true;
/* 629:    */         }
/* 630:    */         else
/* 631:    */         {
/* 632:587 */           if (qm) {
/* 633:587 */             this.intarg[this.intargi] = (-this.intarg[this.intargi]);
/* 634:    */           }
/* 635:588 */           pushChar(this.b);
/* 636:    */         }
/* 637:    */       }
/* 638:    */       catch (Exception e) {}
/* 639:    */     }
/* 640:    */   }
/* 641:    */   
/* 642:    */   private void addLine()
/* 643:    */   {
/* 644:595 */     this.term.draw_cursor();
/* 645:596 */     this.y -= this.char_height;
/* 646:597 */     this.term.scroll_window(this.region_y1 * this.char_height, this.region_y2 * this.char_height, -this.char_height, this.bground, this.smooth);
/* 647:598 */     this.term.setCursor(this.x, this.y);
/* 648:599 */     this.term.draw_cursor();
/* 649:    */   }
/* 650:    */   
/* 651:602 */   private static int[] ENTER = { 13 };
/* 652:603 */   private static int[] BS = { 127 };
/* 653:604 */   private static int[] DEL = { 127 };
/* 654:605 */   private static int[] ESC = { 27 };
/* 655:606 */   private static int[] UP = { 27, 91, 65 };
/* 656:607 */   private static int[] DOWN = { 27, 91, 66 };
/* 657:608 */   private static int[] RIGHT = { 27, 91, 67 };
/* 658:609 */   private static int[] LEFT = { 27, 91, 68 };
/* 659:610 */   private static int[] F1 = { 27, 91, 80 };
/* 660:611 */   private static int[] F2 = { 27, 91, 81 };
/* 661:612 */   private static int[] F3 = { 27, 91, 82 };
/* 662:613 */   private static int[] F4 = { 27, 91, 83 };
/* 663:614 */   private static int[] F5 = { 27, 91, 116 };
/* 664:615 */   private static int[] F6 = { 27, 91, 117 };
/* 665:616 */   private static int[] F7 = { 27, 91, 118 };
/* 666:617 */   private static int[] F8 = { 27, 91, 73 };
/* 667:618 */   private static int[] F9 = { 27, 91, 119 };
/* 668:619 */   private static int[] F10 = { 27, 91, 120 };
/* 669:620 */   private static int[] DSR = { 27, 91, 48, 110 };
/* 670:621 */   private static int[] CPR = { 27, 91 };
/* 671:    */   
/* 672:    */   public int[] getCodeENTER()
/* 673:    */   {
/* 674:623 */     return ENTER;
/* 675:    */   }
/* 676:    */   
/* 677:    */   public int[] getCodeBS()
/* 678:    */   {
/* 679:624 */     return BS;
/* 680:    */   }
/* 681:    */   
/* 682:    */   public int[] getCodeDEL()
/* 683:    */   {
/* 684:625 */     return DEL;
/* 685:    */   }
/* 686:    */   
/* 687:    */   public int[] getCodeESC()
/* 688:    */   {
/* 689:626 */     return ESC;
/* 690:    */   }
/* 691:    */   
/* 692:    */   public int[] getCodeUP()
/* 693:    */   {
/* 694:627 */     return UP;
/* 695:    */   }
/* 696:    */   
/* 697:    */   public int[] getCodeDOWN()
/* 698:    */   {
/* 699:628 */     return DOWN;
/* 700:    */   }
/* 701:    */   
/* 702:    */   public int[] getCodeRIGHT()
/* 703:    */   {
/* 704:629 */     return RIGHT;
/* 705:    */   }
/* 706:    */   
/* 707:    */   public int[] getCodeLEFT()
/* 708:    */   {
/* 709:630 */     return LEFT;
/* 710:    */   }
/* 711:    */   
/* 712:    */   public int[] getCodeF1()
/* 713:    */   {
/* 714:631 */     return F1;
/* 715:    */   }
/* 716:    */   
/* 717:    */   public int[] getCodeF2()
/* 718:    */   {
/* 719:632 */     return F2;
/* 720:    */   }
/* 721:    */   
/* 722:    */   public int[] getCodeF3()
/* 723:    */   {
/* 724:633 */     return F3;
/* 725:    */   }
/* 726:    */   
/* 727:    */   public int[] getCodeF4()
/* 728:    */   {
/* 729:634 */     return F4;
/* 730:    */   }
/* 731:    */   
/* 732:    */   public int[] getCodeF5()
/* 733:    */   {
/* 734:635 */     return F5;
/* 735:    */   }
/* 736:    */   
/* 737:    */   public int[] getCodeF6()
/* 738:    */   {
/* 739:636 */     return F6;
/* 740:    */   }
/* 741:    */   
/* 742:    */   public int[] getCodeF7()
/* 743:    */   {
/* 744:637 */     return F7;
/* 745:    */   }
/* 746:    */   
/* 747:    */   public int[] getCodeF8()
/* 748:    */   {
/* 749:638 */     return F8;
/* 750:    */   }
/* 751:    */   
/* 752:    */   public int[] getCodeF9()
/* 753:    */   {
/* 754:639 */     return F9;
/* 755:    */   }
/* 756:    */   
/* 757:    */   public int[] getCodeF10()
/* 758:    */   {
/* 759:640 */     return F10;
/* 760:    */   }
/* 761:    */   
/* 762:    */   public int[] getDSR()
/* 763:    */   {
/* 764:641 */     return DSR;
/* 765:    */   }
/* 766:    */   
/* 767:    */   public int[] getCPR()
/* 768:    */   {
/* 769:642 */     return CPR;
/* 770:    */   }
/* 771:    */   
/* 772:    */   public void reset()
/* 773:    */   {
/* 774:645 */     this.arch = System.getProperty("os.name");
/* 775:646 */     this.term_width = this.term.getColumnCount();
/* 776:647 */     this.term_height = this.term.getRowCount();
/* 777:648 */     this.char_width = this.term.getCharWidth();
/* 778:649 */     this.char_height = this.term.getCharHeight();
/* 779:    */     
/* 780:651 */     this.fground = this.bfground;
/* 781:652 */     this.term.setFGround(this.fground);
/* 782:653 */     this.bground = this.bbground;
/* 783:654 */     this.term.setBGround(this.bground);
/* 784:    */     
/* 785:656 */     this.cursor_appl = false;
/* 786:657 */     this.ansi = false;
/* 787:658 */     this.col132 = false;
/* 788:659 */     this.smooth = false;
/* 789:660 */     this.reverse = false;
/* 790:661 */     this.org_rel = false;
/* 791:662 */     this.wrap = false;
/* 792:663 */     this.repeat = false;
/* 793:664 */     this.interlace = false;
/* 794:665 */     this.newline = false;
/* 795:    */     
/* 796:667 */     this.tab = new boolean[this.term_width];
/* 797:668 */     for (this.x = 0; this.x < this.term_width; this.x += 1) {
/* 798:669 */       if (this.x % 8 == 0) {
/* 799:669 */         this.tab[this.x] = true;
/* 800:    */       } else {
/* 801:670 */         this.tab[this.x] = false;
/* 802:    */       }
/* 803:    */     }
/* 804:673 */     this.term.setFont(0);
/* 805:674 */     this.char_set = 0;
/* 806:675 */     this.g0_graph = false;
/* 807:676 */     this.x = 0;
/* 808:677 */     this.y = this.char_height;
/* 809:678 */     this.region_y1 = 1;
/* 810:679 */     this.region_y2 = this.term_height;
/* 811:680 */     this.term.clear_area(this.x, this.y - this.char_height, this.term_width * this.char_width, this.term_height * this.char_height);
/* 812:    */     
/* 813:682 */     this.term.redraw(this.x, this.y - this.char_height, this.term_width * this.char_width - this.x, this.term_height * this.char_height - this.y + this.char_height);
/* 814:    */     
/* 815:    */ 
/* 816:685 */     this.term.setCursor(this.x, this.y);
/* 817:686 */     this.term.draw_cursor();
/* 818:    */   }
/* 819:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.VT100
 * JD-Core Version:    0.7.0.1
 */