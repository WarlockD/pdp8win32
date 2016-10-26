/*   1:    */ package Logic;
/*   2:    */ 
/*   3:    */ import Devices.LE8;
/*   4:    */ import java.awt.Toolkit;
/*   5:    */ import java.util.logging.Level;
/*   6:    */ import java.util.logging.Logger;
/*   7:    */ import javax.swing.text.BadLocationException;
/*   8:    */ import javax.swing.text.Document;
/*   9:    */ 
/*  10:    */ public class LPT
/*  11:    */   implements Device, Constants
/*  12:    */ {
/*  13: 20 */   public static int DevId57 = 47;
/*  14: 21 */   public static int DevId66 = 54;
/*  15: 23 */   private int curdev = DevId57;
/*  16:    */   public BusRegMem data;
/*  17:    */   public LE8 le8;
/*  18: 26 */   private boolean lptintena = false;
/*  19: 27 */   public boolean lptflag = false;
/*  20: 28 */   public boolean errflag = false;
/*  21: 29 */   private boolean readflag = false;
/*  22:    */   public StringBuilder buffer;
/*  23: 32 */   private int readbuf = 0;
/*  24: 34 */   private byte[] docbytes = null;
/*  25: 36 */   private boolean docLoaded = false;
/*  26: 37 */   private int docpos = 0;
/*  27: 38 */   private int doclength = 0;
/*  28: 39 */   public boolean lptlinewise = false;
/*  29:    */   private VirTimer lpttim;
/*  30:    */   private VirTimer lptflush;
/*  31:    */   private VirTimer readtim;
/*  32: 45 */   public boolean lptonline = true;
/*  33: 46 */   private int lptlines = 0;
/*  34: 47 */   private int lptpos = 0;
/*  35: 48 */   private int lptcnt = 0;
/*  36: 49 */   public boolean lptfast = false;
/*  37:    */   
/*  38:    */   public LPT(BusRegMem data)
/*  39:    */   {
/*  40: 53 */     this.data = data;
/*  41: 54 */     this.le8 = new LE8(this);
/*  42: 55 */     this.le8.setVisible(true);
/*  43: 56 */     this.buffer = new StringBuilder(256);
/*  44:    */     
/*  45: 58 */     VirListener lptaction = new VirListener()
/*  46:    */     {
/*  47:    */       public void actionPerformed()
/*  48:    */       {
/*  49: 62 */         LPT.this.setLPT(true);
/*  50:    */       }
/*  51: 64 */     };
/*  52: 65 */     this.lpttim = new VirTimer(data.virqueue, 60000, lptaction);
/*  53: 66 */     this.lpttim.setRepeats(false);
/*  54:    */     
/*  55: 68 */     VirListener doflush = new VirListener()
/*  56:    */     {
/*  57:    */       public void actionPerformed()
/*  58:    */       {
/*  59: 72 */         LPT.this.setFlush();
/*  60:    */       }
/*  61: 74 */     };
/*  62: 75 */     this.lptflush = new VirTimer(data.virqueue, 10000000, doflush);
/*  63: 76 */     this.lptflush.setRepeats(false);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void Decode(int devcode, int opcode)
/*  67:    */   {
/*  68: 81 */     if (devcode == DevId66) {
/*  69: 82 */       switch (opcode)
/*  70:    */       {
/*  71:    */       case 0: 
/*  72:    */         break;
/*  73:    */       case 1: 
/*  74:    */         break;
/*  75:    */       case 2: 
/*  76:    */         break;
/*  77:    */       case 3: 
/*  78:    */         break;
/*  79:    */       case 4: 
/*  80:    */         break;
/*  81:    */       case 5: 
/*  82:    */         break;
/*  83:    */       case 6: 
/*  84:    */         break;
/*  85:    */       }
/*  86:    */     }
/*  87:117 */     if (devcode == DevId57) {
/*  88:118 */       switch (opcode)
/*  89:    */       {
/*  90:    */       case 0: 
/*  91:    */         break;
/*  92:    */       case 1: 
/*  93:    */         break;
/*  94:    */       case 2: 
/*  95:123 */         this.data.c0c1c2 = 3;
/*  96:124 */         break;
/*  97:    */       case 3: 
/*  98:    */         break;
/*  99:    */       case 4: 
/* 100:    */         break;
/* 101:    */       case 5: 
/* 102:    */         break;
/* 103:    */       case 6: 
/* 104:    */         break;
/* 105:    */       }
/* 106:    */     }
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void Execute(int devcode, int opcode)
/* 110:    */   {
/* 111:140 */     this.curdev = devcode;
/* 112:141 */     if (devcode == DevId66) {
/* 113:142 */       switch (opcode)
/* 114:    */       {
/* 115:    */       case 0: 
/* 116:144 */         setLPT(false);
/* 117:145 */         lptClear();
/* 118:146 */         break;
/* 119:    */       case 1: 
/* 120:148 */         this.data.skipbus = (this.lptflag);
/* 121:149 */         break;
/* 122:    */       case 2: 
/* 123:151 */         setLPT(false);
/* 124:152 */         break;
/* 125:    */       case 3: 
/* 126:154 */         this.data.skipbus = (!this.lptonline);
/* 127:155 */         break;
/* 128:    */       case 4: 
/* 129:157 */         lptChar(this.data.data & 0x7F);
/* 130:158 */         break;
/* 131:    */       case 5: 
/* 132:160 */         this.lptintena = true;
/* 133:161 */         setIntReq();
/* 134:162 */         break;
/* 135:    */       case 6: 
/* 136:164 */         setLPT(false);
/* 137:165 */         lptChar(this.data.data & 0x7F);
/* 138:166 */         break;
/* 139:    */       case 7: 
/* 140:168 */         this.lptintena = false;
/* 141:169 */         clearIntReq();
/* 142:    */       }
/* 143:    */     }
/* 144:197 */     if (devcode == DevId57) {
/* 145:198 */       switch (opcode)
/* 146:    */       {
/* 147:    */       case 0: 
/* 148:200 */         this.data.skipbus = this.lptflag;
/* 149:201 */         if (this.lptflag) {
/* 150:201 */           setLPT(false);
/* 151:    */         }
/* 152:    */         break;
/* 153:    */       case 1: 
/* 154:204 */         this.data.skipbus = this.readflag;
/* 155:205 */         break;
/* 156:    */       case 2: 
/* 157:207 */         this.data.data = this.readbuf;
/* 158:208 */         break;
/* 159:    */       case 3: 
/* 160:210 */         this.readflag = false;
/* 161:211 */         getChar();
/* 162:212 */         break;
/* 163:    */       case 4: 
/* 164:214 */         lptChar((this.data.data ^ 0xFFFFFFFF) & 0x7F);
/* 165:215 */         break;
/* 166:    */       case 5: 
/* 167:217 */         this.lptintena = true;
/* 168:218 */         setIntReq();
/* 169:219 */         break;
/* 170:    */       case 6: 
/* 171:221 */         this.lptintena = false;
/* 172:222 */         clearIntReq();
/* 173:223 */         break;
/* 174:    */       }
/* 175:    */     }
/* 176:    */   }
/* 177:    */   
/* 178:    */   private void lptChar(int lptdata)
/* 179:    */   {
/* 180:233 */     if (setLine(lptdata) == 0)
/* 181:    */     {
/* 182:234 */       if (this.curdev == DevId57) {
/* 183:235 */         this.lpttim.setInitialDelay(60000);
/* 184:    */       } else {
/* 185:237 */         switch (lptdata)
/* 186:    */         {
/* 187:    */         case 10: 
/* 188:239 */           this.lpttim.setInitialDelay(200000);
/* 189:240 */           break;
/* 190:    */         case 12: 
/* 191:242 */           this.lpttim.setInitialDelay(10000000);
/* 192:243 */           break;
/* 193:    */         default: 
/* 194:245 */           this.lpttim.setInitialDelay(100);
/* 195:246 */           if (this.lptcnt % 24 == 0) {
/* 196:246 */             this.lpttim.setInitialDelay(400000);
/* 197:    */           }
/* 198:    */           break;
/* 199:    */         }
/* 200:    */       }
/* 201:249 */       this.lpttim.start();
/* 202:250 */       this.lptcnt += 1;
/* 203:251 */       if (this.lptflush.expirationTime < this.data.pdp8time + this.lpttim.expirationTime) {
/* 204:252 */         this.lptflush.restart();
/* 205:    */       }
/* 206:    */     }
/* 207:    */   }
/* 208:    */   
/* 209:    */   private void lptClear()
/* 210:    */   {
/* 211:258 */     this.le8.clearEditor();
/* 212:259 */     this.lpttim.start();
/* 213:260 */     this.lptlinewise = false;
/* 214:261 */     this.lptfast = true;
/* 215:    */   }
/* 216:    */   
/* 217:    */   private void setLPT(boolean set)
/* 218:    */   {
/* 219:265 */     if (set)
/* 220:    */     {
/* 221:266 */       this.lptflag = true;
/* 222:267 */       setIntReq();
/* 223:    */     }
/* 224:    */     else
/* 225:    */     {
/* 226:269 */       this.lpttim.stop();
/* 227:270 */       this.lptflag = false;
/* 228:271 */       this.errflag = false;
/* 229:272 */       clearIntReq();
/* 230:    */     }
/* 231:    */   }
/* 232:    */   
/* 233:    */   private int setLine(int newbyte)
/* 234:    */   {
/* 235:277 */     if (!this.lptonline) {
/* 236:278 */       return -1;
/* 237:    */     }
/* 238:280 */     if (((newbyte == 0 ? 1 : 0) | (newbyte == 26 ? 1 : 0)) != 0) {
/* 239:281 */       return 0;
/* 240:    */     }
/* 241:283 */     if (((this.lptpos == 255 ? 1 : 0) | (newbyte == 127 ? 1 : 0)) != 0)
/* 242:    */     {
/* 243:284 */       this.buffer = new StringBuilder(300);
/* 244:285 */       this.lptpos = 0;
/* 245:286 */       return 0;
/* 246:    */     }
/* 247:288 */     if (newbyte == 7)
/* 248:    */     {
/* 249:289 */       Toolkit.getDefaultToolkit().beep();
/* 250:290 */       return 0;
/* 251:    */     }
/* 252:292 */     if (newbyte == 8)
/* 253:    */     {
/* 254:293 */       if (this.buffer.length() > 0)
/* 255:    */       {
/* 256:294 */         this.buffer.deleteCharAt(this.buffer.length() - 1);
/* 257:295 */         this.lptpos -= 1;
/* 258:    */       }
/* 259:297 */       return 0;
/* 260:    */     }
/* 261:299 */     if (newbyte == 13)
/* 262:    */     {
/* 263:300 */       this.lptpos = 0;
/* 264:301 */       this.lptcnt = 0;
/* 265:302 */       return 0;
/* 266:    */     }
/* 267:304 */     if ((newbyte != 10) && (this.lptpos < this.buffer.length()))
/* 268:    */     {
/* 269:305 */       if (newbyte != 32)
/* 270:    */       {
/* 271:306 */         this.buffer.deleteCharAt(this.lptpos);
/* 272:307 */         this.buffer.insert(this.lptpos, (char)newbyte);
/* 273:    */       }
/* 274:309 */       this.lptpos += 1;
/* 275:    */     }
/* 276:310 */     else if (newbyte != 10)
/* 277:    */     {
/* 278:311 */       this.buffer.append((char)newbyte);
/* 279:312 */       this.lptpos += 1;
/* 280:    */     }
/* 281:314 */     if ((newbyte == 12) || (newbyte == 10))
/* 282:    */     {
/* 283:315 */       this.buffer.append(eol);
/* 284:316 */       this.le8.addEditor(this.buffer);
/* 285:317 */       this.buffer = new StringBuilder(300);
/* 286:318 */       this.lptpos = 0;
/* 287:319 */       this.lptcnt = 0;
/* 288:320 */       if (this.lptlinewise) {
/* 289:320 */         this.le8.setEditor();
/* 290:    */       }
/* 291:321 */       this.lptlines += 1;
/* 292:    */     }
/* 293:323 */     if ((newbyte == 12) || (this.lptlines > 100))
/* 294:    */     {
/* 295:324 */       if (!this.lptfast) {
/* 296:324 */         this.le8.setEditor();
/* 297:    */       }
/* 298:325 */       this.lptlines = 0;
/* 299:    */     }
/* 300:327 */     return 0;
/* 301:    */   }
/* 302:    */   
/* 303:    */   private void getChar()
/* 304:    */   {
/* 305:333 */     clearIntReq();
/* 306:334 */     if (!this.docLoaded)
/* 307:    */     {
/* 308:335 */       Document doc = this.le8.getDocument();
/* 309:    */       try
/* 310:    */       {
/* 311:337 */         String doctext = doc.getText(0, doc.getLength());
/* 312:338 */         this.docbytes = doctext.replace("\n", "\r\n").getBytes();
/* 313:    */       }
/* 314:    */       catch (BadLocationException ex)
/* 315:    */       {
/* 316:340 */         Logger.getLogger(LPT.class.getName()).log(Level.SEVERE, null, ex);
/* 317:    */       }
/* 318:342 */       this.doclength = this.docbytes.length;
/* 319:343 */       if (this.doclength == 0)
/* 320:    */       {
/* 321:344 */         this.readbuf = 2048;
/* 322:    */       }
/* 323:    */       else
/* 324:    */       {
/* 325:346 */         this.docLoaded = true;
/* 326:347 */         this.docpos = 0;
/* 327:    */       }
/* 328:    */     }
/* 329:350 */     if (this.docLoaded) {
/* 330:351 */       if (this.docpos < this.doclength)
/* 331:    */       {
/* 332:352 */         this.readbuf = this.docbytes[this.docpos];
/* 333:353 */         this.docpos += 1;
/* 334:    */       }
/* 335:    */       else
/* 336:    */       {
/* 337:355 */         this.readbuf = 2048;
/* 338:356 */         this.docLoaded = false;
/* 339:357 */         this.docbytes = null;
/* 340:    */       }
/* 341:    */     }
/* 342:360 */     this.readflag = true;
/* 343:361 */     setIntReq();
/* 344:    */   }
/* 345:    */   
/* 346:    */   private void setFlush()
/* 347:    */   {
/* 348:366 */     this.le8.addEditor(this.buffer);
/* 349:367 */     this.buffer = new StringBuilder(300);
/* 350:368 */     this.le8.setEditor();
/* 351:369 */     this.lptfast = false;
/* 352:370 */     this.lptpos = 0;
/* 353:    */   }
/* 354:    */   
/* 355:    */   public void clearIntReq()
/* 356:    */   {
/* 357:374 */     if (!this.lptintena) {
/* 358:374 */       this.data.setIntReq(this.curdev, false);
/* 359:375 */     } else if (((!this.lptflag ? 1 : 0) & (!this.readflag ? 1 : 0) & (!this.errflag ? 1 : 0)) != 0) {
/* 360:376 */       this.data.setIntReq(this.curdev, false);
/* 361:    */     }
/* 362:    */   }
/* 363:    */   
/* 364:    */   public void setIntReq()
/* 365:    */   {
/* 366:381 */     if (!this.lptintena) {
/* 367:381 */       this.data.setIntReq(this.curdev, false);
/* 368:382 */     } else if (((this.lptflag == true ? 1 : 0) | (this.readflag == true ? 1 : 0) | (this.errflag == true ? 1 : 0)) != 0) {
/* 369:383 */       this.data.setIntReq(this.curdev, true);
/* 370:    */     } else {
/* 371:385 */       this.data.setIntReq(this.curdev, false);
/* 372:    */     }
/* 373:    */   }
/* 374:    */   
/* 375:    */   public void ClearFlags(int devcode)
/* 376:    */   {
/* 377:389 */     this.lpttim.stop();
/* 378:390 */     this.lptflush.stop();
/* 379:391 */     this.lptflag = false;
/* 380:392 */     this.errflag = false;
/* 381:393 */     this.readflag = false;
/* 382:394 */     this.lptintena = false;
/* 383:395 */     this.lptonline = true;
/* 384:396 */     this.data.setIntReq(DevId66, this.lptintena);
/* 385:    */   }
/* 386:    */   
/* 387:    */   public void Interrupt(int command) {}
/* 388:    */   
/* 389:    */   public void ClearRun(boolean run) {}
/* 390:    */   
/* 391:    */   public void CloseDev(int devcode) {}
/* 392:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.LPT
 * JD-Core Version:    0.7.0.1
 */