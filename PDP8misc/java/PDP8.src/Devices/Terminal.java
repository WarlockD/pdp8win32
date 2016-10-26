/*   1:    */ package Devices;
/*   2:    */ 
/*   3:    */ import Logic.BusRegMem;
/*   4:    */ import Logic.TTY;
/*   5:    */ import java.awt.Color;
/*   6:    */ import java.awt.Component;
/*   7:    */ import java.awt.Container;
/*   8:    */ import java.awt.Dimension;
/*   9:    */ import java.awt.Font;
/*  10:    */ import java.awt.FontMetrics;
/*  11:    */ import java.awt.Graphics;
/*  12:    */ import java.awt.Graphics2D;
/*  13:    */ import java.awt.RenderingHints;
/*  14:    */ import java.awt.Toolkit;
/*  15:    */ import java.awt.event.ActionEvent;
/*  16:    */ import java.awt.event.ActionListener;
/*  17:    */ import java.awt.event.ItemEvent;
/*  18:    */ import java.awt.event.ItemListener;
/*  19:    */ import java.awt.event.KeyEvent;
/*  20:    */ import java.awt.event.KeyListener;
/*  21:    */ import java.awt.event.WindowAdapter;
/*  22:    */ import java.awt.event.WindowEvent;
/*  23:    */ import java.awt.geom.AffineTransform;
/*  24:    */ import java.awt.image.BufferedImage;
/*  25:    */ import java.io.BufferedOutputStream;
/*  26:    */ import java.io.File;
/*  27:    */ import java.io.FileInputStream;
/*  28:    */ import java.io.FileOutputStream;
/*  29:    */ import java.io.IOException;
/*  30:    */ import java.io.InputStream;
/*  31:    */ import java.io.OutputStream;
/*  32:    */ import java.io.PipedInputStream;
/*  33:    */ import java.io.PipedOutputStream;
/*  34:    */ import java.io.PrintStream;
/*  35:    */ import javax.swing.JCheckBoxMenuItem;
/*  36:    */ import javax.swing.JFileChooser;
/*  37:    */ import javax.swing.JFrame;
/*  38:    */ import javax.swing.JMenu;
/*  39:    */ import javax.swing.JMenuBar;
/*  40:    */ import javax.swing.JMenuItem;
/*  41:    */ import javax.swing.JOptionPane;
/*  42:    */ import javax.swing.JPanel;
/*  43:    */ import javax.swing.filechooser.FileFilter;
/*  44:    */ 
/*  45:    */ public class Terminal
/*  46:    */   extends JPanel
/*  47:    */   implements KeyListener, ActionListener, ItemListener, Term
/*  48:    */ {
/*  49: 26 */   static String COPYRIGHT = "PDP8 - VT100 1.0\nCopyright (C) 2004-2012 W. van der Mark\nThis software is licensed under GNU LGPL.";
/*  50:    */   private OutputStream out;
/*  51:    */   private SendOutput output;
/*  52:    */   private SendFileInput fileinput;
/*  53:    */   private Thread fileinth;
/*  54:    */   private InputStream in;
/*  55:    */   private ReceiveInput input;
/*  56:    */   private ReceiveFileOutput fileoutput;
/*  57: 37 */   private Emulator emulator = null;
/*  58:    */   private JFrame frame;
/*  59:    */   private VT100Lamps lamps;
/*  60:    */   private BufferedImage img;
/*  61:    */   private BufferedImage background;
/*  62:    */   private Graphics2D cursor_graphics;
/*  63:    */   private Graphics2D graphics;
/*  64: 46 */   private Color bground = Color.white;
/*  65: 47 */   private Color fground = Color.black;
/*  66: 48 */   private Component term_area = null;
/*  67:    */   private Font primfont;
/*  68:    */   private Font font;
/*  69: 52 */   private int term_width = 82;
/*  70: 53 */   private int term_height = 24;
/*  71: 55 */   private int x = 0;
/*  72: 56 */   private int y = 0;
/*  73: 57 */   private int descent = 0;
/*  74:    */   private int char_width;
/*  75:    */   private int char_height;
/*  76: 61 */   private boolean underline = false;
/*  77:    */   private int save_width;
/*  78: 63 */   private int style = 0;
/*  79: 64 */   private AffineTransform at = new AffineTransform();
/*  80: 65 */   private int clip = 0;
/*  81: 66 */   private int lampState = 64;
/*  82: 68 */   private boolean antialiasing = true;
/*  83: 69 */   private int line_space = 1;
/*  84:    */   private TTY channel;
/*  85:    */   
/*  86:    */   public Terminal(TTY channel)
/*  87:    */   {
/*  88: 74 */     this.channel = channel;
/*  89: 75 */     this.font = new Font("Monospaced", 0, 12);
/*  90: 76 */     this.at.setToScale(1.0D, 1.0D);
/*  91: 77 */     this.primfont = this.font.deriveFont(0, this.at);
/*  92: 78 */     this.font = this.primfont.deriveFont(0);
/*  93:    */     
/*  94: 80 */     this.img = new BufferedImage(1, 1, 1);
/*  95: 81 */     this.graphics = ((Graphics2D)this.img.getGraphics());
/*  96: 82 */     this.graphics.setFont(this.font);
/*  97: 83 */     FontMetrics fo = this.graphics.getFontMetrics();
/*  98: 84 */     this.descent = fo.getDescent();
/*  99: 85 */     this.char_width = fo.charWidth('W');
/* 100: 86 */     this.save_width = this.char_width;
/* 101: 87 */     this.char_height = (fo.getHeight() + this.line_space * 2);
/* 102: 88 */     this.descent += this.line_space;
/* 103:    */     
/* 104: 90 */     this.img.flush();
/* 105: 91 */     this.graphics.dispose();
/* 106:    */     
/* 107: 93 */     this.background = new BufferedImage(this.char_width, this.char_height, 1);
/* 108: 94 */     Graphics2D foog = (Graphics2D)this.background.getGraphics();
/* 109: 95 */     foog.setColor(this.bground);
/* 110: 96 */     foog.fillRect(0, 0, this.char_width, this.char_height);
/* 111: 97 */     foog.dispose();
/* 112:    */     
/* 113:    */ 
/* 114:100 */     this.img = new BufferedImage(getTermWidth(), getTermHeight(), 1);
/* 115:101 */     this.graphics = ((Graphics2D)this.img.getGraphics());
/* 116:102 */     this.graphics.setFont(this.font);
/* 117:    */     
/* 118:104 */     clear();
/* 119:    */     
/* 120:106 */     this.cursor_graphics = ((Graphics2D)this.img.getGraphics());
/* 121:107 */     this.cursor_graphics.setColor(this.fground);
/* 122:108 */     this.cursor_graphics.setXORMode(this.bground);
/* 123:    */     
/* 124:110 */     setAntiAliasing(this.antialiasing);
/* 125:    */     
/* 126:112 */     this.term_area = this;
/* 127:    */     
/* 128:114 */     JPanel panel = this;
/* 129:115 */     panel.setPreferredSize(new Dimension(getTermWidth(), getTermHeight()));
/* 130:116 */     panel.setSize(getTermWidth(), getTermHeight());
/* 131:117 */     panel.setFocusable(true);
/* 132:118 */     panel.enableInputMethods(true);
/* 133:119 */     panel.setFocusTraversalKeysEnabled(false);
/* 134:    */     
/* 135:121 */     Terminal term = this;
/* 136:    */     
/* 137:123 */     this.frame = new JFrame("VT100 Terminal");
/* 138:    */     
/* 139:125 */     this.frame.addWindowListener(new WindowAdapter()
/* 140:    */     {
/* 141:    */       public void windowClosing(WindowEvent evt)
/* 142:    */       {
/* 143:128 */         Terminal.this.exitForm(evt);
/* 144:    */       }
/* 145:131 */     });
/* 146:132 */     this.lamps = new VT100Lamps();
/* 147:133 */     Dimension padim = this.lamps.setPanel(term);
/* 148:134 */     JMenuBar mb = term.getJMenuBar();
/* 149:135 */     this.frame.setJMenuBar(mb);
/* 150:136 */     this.frame.getContentPane().add(this.lamps, "Center");
/* 151:137 */     this.frame.setBackground(Color.darkGray);
/* 152:138 */     this.frame.pack();
/* 153:139 */     term.setVisible(true);
/* 154:140 */     this.frame.setVisible(true);
/* 155:141 */     setLEDs(0);
/* 156:142 */     term.setFrame(term);
/* 157:    */     try
/* 158:    */     {
/* 159:145 */       this.out = new PipedOutputStream();
/* 160:146 */       this.in = new PipedInputStream();
/* 161:147 */       channel.setInputOutputStream(new PipedInputStream((PipedOutputStream)this.out), new PipedOutputStream((PipedInputStream)this.in));
/* 162:    */     }
/* 163:    */     catch (Exception e) {}
/* 164:153 */     this.output = new SendOutput();
/* 165:154 */     new Thread(this.output, "Terminal-Output").start();
/* 166:155 */     this.emulator = new VT100(this, this.in);
/* 167:156 */     this.input = new ReceiveInput();
/* 168:157 */     new Thread(this.input, "Terminal-Input").start();
/* 169:158 */     this.fileinput = new SendFileInput();
/* 170:159 */     this.fileoutput = new ReceiveFileOutput();
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void setFrame(Component term_area)
/* 174:    */   {
/* 175:163 */     this.term_area = term_area;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void paintComponent(Graphics g)
/* 179:    */   {
/* 180:168 */     g.drawImage(this.img, 0, 0, this.term_area);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public void update(Graphics g) {}
/* 184:    */   
/* 185:    */   public void paint(Graphics g)
/* 186:    */   {
/* 187:177 */     super.paint(g);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void processKeyEvent(KeyEvent e)
/* 191:    */   {
/* 192:183 */     int id = e.getID();
/* 193:184 */     if (id == 401) {
/* 194:184 */       keyPressed(e);
/* 195:185 */     } else if ((id != 402) && 
/* 196:186 */       (id == 400)) {
/* 197:186 */       keyTyped(e);
/* 198:    */     }
/* 199:    */   }
/* 200:    */   
/* 201:189 */   int[] obuffer = new int[3];
/* 202:    */   
/* 203:    */   public void keyPressed(KeyEvent e)
/* 204:    */   {
/* 205:192 */     int keycode = e.getKeyCode();
/* 206:193 */     int[] code = null;
/* 207:194 */     switch (keycode)
/* 208:    */     {
/* 209:    */     case 16: 
/* 210:    */     case 17: 
/* 211:    */     case 18: 
/* 212:    */     case 20: 
/* 213:    */     case 65406: 
/* 214:200 */       return;
/* 215:    */     case 10: 
/* 216:202 */       code = this.emulator.getCodeENTER();
/* 217:203 */       break;
/* 218:    */     case 8: 
/* 219:205 */       code = this.emulator.getCodeBS();
/* 220:206 */       break;
/* 221:    */     case 127: 
/* 222:208 */       code = this.emulator.getCodeDEL();
/* 223:209 */       break;
/* 224:    */     case 27: 
/* 225:211 */       code = this.emulator.getCodeESC();
/* 226:212 */       break;
/* 227:    */     case 38: 
/* 228:214 */       code = this.emulator.getCodeUP();
/* 229:215 */       break;
/* 230:    */     case 40: 
/* 231:217 */       code = this.emulator.getCodeDOWN();
/* 232:218 */       break;
/* 233:    */     case 39: 
/* 234:220 */       code = this.emulator.getCodeRIGHT();
/* 235:221 */       break;
/* 236:    */     case 37: 
/* 237:223 */       code = this.emulator.getCodeLEFT();
/* 238:224 */       break;
/* 239:    */     case 112: 
/* 240:226 */       code = this.emulator.getCodeF1();
/* 241:227 */       break;
/* 242:    */     case 113: 
/* 243:229 */       code = this.emulator.getCodeF2();
/* 244:230 */       break;
/* 245:    */     case 114: 
/* 246:232 */       code = this.emulator.getCodeF3();
/* 247:233 */       break;
/* 248:    */     case 115: 
/* 249:235 */       code = this.emulator.getCodeF4();
/* 250:236 */       break;
/* 251:    */     case 116: 
/* 252:238 */       code = this.emulator.getCodeF5();
/* 253:239 */       break;
/* 254:    */     case 117: 
/* 255:241 */       code = this.emulator.getCodeF6();
/* 256:242 */       break;
/* 257:    */     case 118: 
/* 258:244 */       code = this.emulator.getCodeF7();
/* 259:245 */       break;
/* 260:    */     case 119: 
/* 261:247 */       code = this.emulator.getCodeF8();
/* 262:248 */       break;
/* 263:    */     case 120: 
/* 264:250 */       code = this.emulator.getCodeF9();
/* 265:251 */       break;
/* 266:    */     case 121: 
/* 267:253 */       code = this.emulator.getCodeF10();
/* 268:    */     }
/* 269:256 */     if (code != null)
/* 270:    */     {
/* 271:257 */       this.output.setOutput(code, code.length);
/* 272:258 */       e.consume();
/* 273:259 */       return;
/* 274:    */     }
/* 275:261 */     char keychar = e.getKeyChar();
/* 276:    */   }
/* 277:    */   
/* 278:    */   public void keyTyped(KeyEvent e)
/* 279:    */   {
/* 280:265 */     char keychar = e.getKeyChar();
/* 281:266 */     int modifiers = e.getModifiers();
/* 282:267 */     int xx = modifiers & 0x2;
/* 283:268 */     if (((!Character.isISOControl(keychar) ? 1 : 0) & (keychar < '' ? 1 : 0) | (xx != 0 ? 1 : 0)) != 0)
/* 284:    */     {
/* 285:269 */       this.obuffer[0] = e.getKeyChar();
/* 286:270 */       this.output.setOutput(this.obuffer, 1);
/* 287:    */     }
/* 288:    */   }
/* 289:    */   
/* 290:    */   public void sendKeySeq(int[] keyseq)
/* 291:    */   {
/* 292:275 */     this.output.setOutput(keyseq, keyseq.length);
/* 293:    */   }
/* 294:    */   
/* 295:    */   public boolean sendOutByte(int outbyte)
/* 296:    */   {
/* 297:279 */     return this.fileoutput.sendByte(outbyte);
/* 298:    */   }
/* 299:    */   
/* 300:    */   public int getTermWidth()
/* 301:    */   {
/* 302:282 */     return this.char_width * this.term_width;
/* 303:    */   }
/* 304:    */   
/* 305:    */   public int getTermHeight()
/* 306:    */   {
/* 307:283 */     return this.char_height * this.term_height;
/* 308:    */   }
/* 309:    */   
/* 310:    */   public int getCharWidth()
/* 311:    */   {
/* 312:284 */     return this.char_width;
/* 313:    */   }
/* 314:    */   
/* 315:    */   public int getCharHeight()
/* 316:    */   {
/* 317:285 */     return this.char_height;
/* 318:    */   }
/* 319:    */   
/* 320:    */   public int getColumnCount()
/* 321:    */   {
/* 322:286 */     return this.term_width;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public int getRowCount()
/* 326:    */   {
/* 327:287 */     return this.term_height;
/* 328:    */   }
/* 329:    */   
/* 330:    */   public void clear()
/* 331:    */   {
/* 332:290 */     this.graphics.setColor(this.bground);
/* 333:291 */     this.graphics.fillRect(0, 0, this.char_width * this.term_width, this.char_height * this.term_height);
/* 334:292 */     this.graphics.setColor(this.fground);
/* 335:    */   }
/* 336:    */   
/* 337:    */   public void setCursor(int x, int y)
/* 338:    */   {
/* 339:296 */     this.x = x;
/* 340:297 */     this.y = y;
/* 341:    */   }
/* 342:    */   
/* 343:    */   public void setLEDs(int x)
/* 344:    */   {
/* 345:301 */     if (x == 0) {
/* 346:301 */       this.lampState &= 0x1F0;
/* 347:    */     }
/* 348:302 */     if (x > 0) {
/* 349:302 */       this.lampState |= 1 << x - 1;
/* 350:    */     }
/* 351:303 */     if (x < 0) {
/* 352:303 */       this.lampState &= (1 << -x - 1 ^ 0xFFFFFFFF);
/* 353:    */     }
/* 354:304 */     this.lamps.setState(this.lampState);
/* 355:    */   }
/* 356:    */   
/* 357:    */   public void draw_cursor()
/* 358:    */   {
/* 359:308 */     this.cursor_graphics.fillRect(this.x, this.y - this.char_height, this.char_width, this.char_height);
/* 360:309 */     this.term_area.repaint(this.x, this.y - this.char_height, this.char_width, this.char_height);
/* 361:    */   }
/* 362:    */   
/* 363:    */   public void redraw(int x, int y, int width, int height)
/* 364:    */   {
/* 365:313 */     this.term_area.repaint(x, y, width, height);
/* 366:    */   }
/* 367:    */   
/* 368:    */   public void clear_area(int x1, int y1, int x2, int y2)
/* 369:    */   {
/* 370:317 */     for (int i = y1; i < y2; i += this.char_height) {
/* 371:318 */       for (int j = x1; j < x2; j += this.char_width) {
/* 372:319 */         this.graphics.drawImage(this.background, j, i, this.term_area);
/* 373:    */       }
/* 374:    */     }
/* 375:    */   }
/* 376:    */   
/* 377:    */   public void setFont(int attr)
/* 378:    */   {
/* 379:325 */     if (attr == 0)
/* 380:    */     {
/* 381:327 */       this.style = 0;
/* 382:328 */       this.underline = false;
/* 383:    */     }
/* 384:332 */     if (attr == 1) {
/* 385:332 */       this.style = 1;
/* 386:    */     }
/* 387:333 */     if (attr == 2) {
/* 388:333 */       this.underline = true;
/* 389:    */     }
/* 390:334 */     if (attr == 16)
/* 391:    */     {
/* 392:335 */       this.at.setToScale(1.0D, 1.0D);
/* 393:336 */       this.char_width = this.save_width;
/* 394:337 */       this.clip = 0;
/* 395:    */     }
/* 396:339 */     if (attr == 32)
/* 397:    */     {
/* 398:340 */       this.at.setToScale(2.0D, 1.0D);
/* 399:341 */       this.char_width = (2 * this.save_width);
/* 400:342 */       this.clip = 0;
/* 401:    */     }
/* 402:344 */     if (attr == 64)
/* 403:    */     {
/* 404:345 */       this.at.setToScale(2.0D, 2.0D);
/* 405:346 */       this.char_width = (2 * this.save_width);
/* 406:347 */       this.clip = (this.char_height / 2);
/* 407:    */     }
/* 408:349 */     if (attr == 128)
/* 409:    */     {
/* 410:350 */       this.at.setToScale(2.0D, 2.0D);
/* 411:351 */       this.char_width = (2 * this.save_width);
/* 412:352 */       this.clip = (-this.char_height / 2);
/* 413:    */     }
/* 414:354 */     this.font = this.primfont.deriveFont(this.style, this.at);
/* 415:355 */     this.graphics.setFont(this.font);
/* 416:    */   }
/* 417:    */   
/* 418:    */   public void scroll_window(int top, int bot, int dy, Color back, boolean smooth)
/* 419:    */   {
/* 420:360 */     int w = getTermWidth();
/* 421:361 */     if (smooth)
/* 422:    */     {
/* 423:362 */       Color tcolor = this.graphics.getColor();
/* 424:363 */       this.graphics.setColor(back);
/* 425:    */       try
/* 426:    */       {
/* 427:365 */         if (dy > 0) {
/* 428:366 */           for (int yt = top; yt < top + dy; yt++)
/* 429:    */           {
/* 430:367 */             this.graphics.drawLine(0, yt, w, yt);
/* 431:368 */             this.graphics.copyArea(0, yt, w, bot - top, 0, 1);
/* 432:369 */             this.term_area.repaint(0, yt, w, yt + bot - top);
/* 433:370 */             Thread.sleep(10L);
/* 434:    */           }
/* 435:    */         } else {
/* 436:373 */           for (int yt = top; yt > top + dy; yt--)
/* 437:    */           {
/* 438:374 */             this.graphics.drawLine(0, yt + bot - top, w, yt + bot - top);
/* 439:375 */             this.graphics.copyArea(0, yt, w, bot - top, 0, -1);
/* 440:376 */             this.term_area.repaint(0, yt, w, yt + bot - top);
/* 441:377 */             Thread.sleep(10L);
/* 442:    */           }
/* 443:    */         }
/* 444:    */       }
/* 445:    */       catch (Exception e) {}
/* 446:381 */       this.graphics.setColor(tcolor);
/* 447:    */     }
/* 448:    */     else
/* 449:    */     {
/* 450:385 */       this.graphics.copyArea(0, top, w, bot - top, 0, dy);
/* 451:386 */       if (dy > 0) {
/* 452:386 */         clear_area(0, top, w, top + this.char_height);
/* 453:    */       } else {
/* 454:386 */         clear_area(0, bot - this.char_height, w, bot);
/* 455:    */       }
/* 456:387 */       redraw(0, top - this.char_height, w, bot + this.char_height);
/* 457:    */     }
/* 458:    */   }
/* 459:    */   
/* 460:    */   public void drawBytes(byte[] buf, int s, int len, int x, int y)
/* 461:    */   {
/* 462:393 */     this.graphics.drawBytes(buf, s, len, x, y - this.descent);
/* 463:    */   }
/* 464:    */   
/* 465:    */   public void drawChars(char[] buf, int s, int len, int x, int y)
/* 466:    */   {
/* 467:397 */     this.graphics.clipRect(x, y - this.char_height, this.char_width, this.char_height);
/* 468:398 */     this.graphics.drawChars(buf, s, len, x, y - this.descent + this.clip);
/* 469:399 */     if (this.underline) {
/* 470:399 */       this.graphics.drawLine(x, y - 2, x + len * this.char_width, y - 2);
/* 471:    */     }
/* 472:400 */     this.graphics.setClip(null);
/* 473:    */   }
/* 474:    */   
/* 475:    */   public void drawString(String str, int x, int y)
/* 476:    */   {
/* 477:404 */     this.graphics.drawString(str, x, y - this.descent);
/* 478:    */   }
/* 479:    */   
/* 480:    */   public void beep()
/* 481:    */   {
/* 482:408 */     Toolkit.getDefaultToolkit().beep();
/* 483:    */   }
/* 484:    */   
/* 485:    */   public void keyReleased(KeyEvent event) {}
/* 486:    */   
/* 487:    */   public void setLineSpace(int foo)
/* 488:    */   {
/* 489:414 */     this.line_space = foo;
/* 490:    */   }
/* 491:    */   
/* 492:    */   public void setAntiAliasing(boolean foo)
/* 493:    */   {
/* 494:417 */     if (this.graphics == null) {
/* 495:417 */       return;
/* 496:    */     }
/* 497:418 */     this.antialiasing = foo;
/* 498:419 */     Object mode = foo ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
/* 499:    */     
/* 500:    */ 
/* 501:422 */     RenderingHints hints = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, mode);
/* 502:    */     
/* 503:424 */     this.graphics.setRenderingHints(hints);
/* 504:    */   }
/* 505:    */   
/* 506:    */   public void actionPerformed(ActionEvent e)
/* 507:    */   {
/* 508:428 */     String action = e.getActionCommand();
/* 509:429 */     if (action.equals("Open Reader..."))
/* 510:    */     {
/* 511:430 */       openReaderActionPerformed(e);
/* 512:    */     }
/* 513:    */     else
/* 514:    */     {
/* 515:432 */       if (action.equals("Close Reader"))
/* 516:    */       {
/* 517:433 */         this.fileinput.setFileInput(null);
/* 518:434 */         this.channel.setReader(false);
/* 519:435 */         this.emulator.reset();
/* 520:436 */         return;
/* 521:    */       }
/* 522:438 */       if (action.equals("Open Punch..."))
/* 523:    */       {
/* 524:439 */         openPunchActionPerformed(e);
/* 525:    */       }
/* 526:441 */       else if (action.equals("Close Punch"))
/* 527:    */       {
/* 528:442 */         this.fileoutput.setFileOutput(null);
/* 529:    */       }
/* 530:444 */       else if (!action.equals("Loop"))
/* 531:    */       {
/* 532:447 */         if (action.equals("About..."))
/* 533:    */         {
/* 534:448 */           JOptionPane.showMessageDialog(this, COPYRIGHT);
/* 535:449 */           return;
/* 536:    */         }
/* 537:451 */         if (action.equals("Quit")) {
/* 538:452 */           quit();
/* 539:    */         }
/* 540:    */       }
/* 541:    */     }
/* 542:    */   }
/* 543:    */   
/* 544:    */   public void itemStateChanged(ItemEvent e)
/* 545:    */   {
/* 546:457 */     Object source = e.getItemSelectable();
/* 547:458 */     if (e.getStateChange() == 1) {
/* 548:458 */       this.emulator.setLoopback(true);
/* 549:    */     } else {
/* 550:459 */       this.emulator.setLoopback(false);
/* 551:    */     }
/* 552:    */   }
/* 553:    */   
/* 554:    */   private void openReaderActionPerformed(ActionEvent evt)
/* 555:    */   {
/* 556:464 */     JFileChooser chooser = new JFileChooser();
/* 557:465 */     chooser.addChoosableFileFilter(new ImageFileFilter(null));
/* 558:466 */     int option = chooser.showOpenDialog(this);
/* 559:467 */     if (option == 0)
/* 560:    */     {
/* 561:468 */       File file = chooser.getSelectedFile();
/* 562:469 */       if (file != null)
/* 563:    */       {
/* 564:470 */         this.channel.setReader(true);
/* 565:471 */         this.fileinput.setFileInput(file);
/* 566:472 */         this.fileinth = new Thread(this.fileinput, "Terminal-Reader");
/* 567:473 */         this.fileinth.start();
/* 568:    */       }
/* 569:    */       else
/* 570:    */       {
/* 571:474 */         System.out.println("No file selected");
/* 572:    */       }
/* 573:    */     }
/* 574:    */   }
/* 575:    */   
/* 576:    */   private void openPunchActionPerformed(ActionEvent evt)
/* 577:    */   {
/* 578:479 */     JFileChooser chooser = new JFileChooser();
/* 579:480 */     chooser.addChoosableFileFilter(new ImageFileFilter(null));
/* 580:481 */     int option = chooser.showOpenDialog(this);
/* 581:482 */     if (option == 0)
/* 582:    */     {
/* 583:483 */       File file = chooser.getSelectedFile();
/* 584:484 */       if (file != null)
/* 585:    */       {
/* 586:485 */         this.fileoutput.setFileOutput(file);
/* 587:486 */         new Thread(this.fileoutput, "Terminal-Punch").start();
/* 588:487 */         this.emulator.setPunch();
/* 589:    */       }
/* 590:    */     }
/* 591:    */   }
/* 592:    */   
/* 593:    */   public JMenuBar getJMenuBar()
/* 594:    */   {
/* 595:493 */     JMenuBar mb = new JMenuBar();
/* 596:    */     
/* 597:    */ 
/* 598:    */ 
/* 599:497 */     mb.setBackground(Color.darkGray);
/* 600:498 */     mb.setBorder(null);
/* 601:    */     
/* 602:500 */     JMenu m = new JMenu("Reader - Punch");
/* 603:501 */     m.setBackground(Color.darkGray);
/* 604:502 */     m.setForeground(Color.white);
/* 605:503 */     m.setBorder(null);
/* 606:504 */     JMenuItem mi = new JMenuItem("Open Reader...");
/* 607:505 */     mi.addActionListener(this);
/* 608:506 */     mi.setActionCommand("Open Reader...");
/* 609:507 */     mi.setBackground(Color.darkGray);
/* 610:508 */     mi.setForeground(Color.white);
/* 611:509 */     mi.setBorder(null);
/* 612:510 */     m.add(mi);
/* 613:511 */     mi = new JMenuItem("Close Reader");
/* 614:512 */     mi.addActionListener(this);
/* 615:513 */     mi.setActionCommand("Close Reader");
/* 616:514 */     mi.setBackground(Color.darkGray);
/* 617:515 */     mi.setForeground(Color.white);
/* 618:516 */     mi.setBorder(null);
/* 619:517 */     m.add(mi);
/* 620:518 */     m.addSeparator();
/* 621:519 */     mi = new JMenuItem("Open Punch...");
/* 622:520 */     mi.addActionListener(this);
/* 623:521 */     mi.setActionCommand("Open Punch...");
/* 624:522 */     mi.setBackground(Color.darkGray);
/* 625:523 */     mi.setForeground(Color.white);
/* 626:524 */     mi.setBorder(null);
/* 627:525 */     m.add(mi);
/* 628:526 */     mi = new JMenuItem("Close Punch");
/* 629:527 */     mi.addActionListener(this);
/* 630:528 */     mi.setActionCommand("Close Punch");
/* 631:529 */     mi.setBackground(Color.darkGray);
/* 632:530 */     mi.setForeground(Color.white);
/* 633:531 */     mi.setBorder(null);
/* 634:532 */     m.add(mi);
/* 635:533 */     m.addSeparator();
/* 636:534 */     mi = new JMenuItem("Quit");
/* 637:535 */     mi.addActionListener(this);
/* 638:536 */     mi.setActionCommand("Quit");
/* 639:537 */     mi.setBackground(Color.darkGray);
/* 640:538 */     mi.setForeground(Color.white);
/* 641:539 */     mi.setBorder(null);
/* 642:540 */     m.add(mi);
/* 643:541 */     mb.add(m);
/* 644:    */     
/* 645:543 */     m = new JMenu("Test - About");
/* 646:544 */     m.setBackground(Color.darkGray);
/* 647:545 */     m.setForeground(Color.white);
/* 648:546 */     m.setBorder(null);
/* 649:547 */     mi = new JCheckBoxMenuItem("Loopback");
/* 650:548 */     mi.addItemListener(this);
/* 651:549 */     mi.setActionCommand("Loopback");
/* 652:550 */     mi.setBackground(Color.darkGray);
/* 653:551 */     mi.setForeground(Color.white);
/* 654:552 */     mi.setBorder(null);
/* 655:553 */     m.add(mi);
/* 656:554 */     m.addSeparator();
/* 657:555 */     mi = new JMenuItem("About...");
/* 658:556 */     mi.addActionListener(this);
/* 659:557 */     mi.setActionCommand("About...");
/* 660:558 */     mi.setBackground(Color.darkGray);
/* 661:559 */     mi.setForeground(Color.white);
/* 662:560 */     mi.setBorder(null);
/* 663:561 */     m.add(mi);
/* 664:562 */     mb.add(m);
/* 665:    */     
/* 666:564 */     return mb;
/* 667:    */   }
/* 668:    */   
/* 669:    */   private void exitForm(WindowEvent evt)
/* 670:    */   {
/* 671:568 */     this.channel.data.CloseAllDevs();
/* 672:    */   }
/* 673:    */   
/* 674:    */   public void quit() {}
/* 675:    */   
/* 676:    */   public void setFGround(Color f)
/* 677:    */   {
/* 678:576 */     this.fground = f;
/* 679:577 */     this.graphics.setColor(f);
/* 680:    */   }
/* 681:    */   
/* 682:    */   public void setBGround(Color b)
/* 683:    */   {
/* 684:581 */     this.bground = b;
/* 685:582 */     this.background = new BufferedImage(this.char_width, this.char_height, 1);
/* 686:583 */     Graphics2D foog = (Graphics2D)this.background.getGraphics();
/* 687:584 */     foog.setColor(b);
/* 688:585 */     foog.fillRect(0, 0, this.char_width, this.char_height);
/* 689:586 */     foog.dispose();
/* 690:    */   }
/* 691:    */   
/* 692:    */   public Color getFGround()
/* 693:    */   {
/* 694:590 */     return this.fground;
/* 695:    */   }
/* 696:    */   
/* 697:    */   public Color getBGround()
/* 698:    */   {
/* 699:592 */     return this.bground;
/* 700:    */   }
/* 701:    */   
/* 702:    */   public class SendOutput
/* 703:    */     implements Runnable
/* 704:    */   {
/* 705:595 */     private int[] code = new int[4];
/* 706:    */     private int codelen;
/* 707:    */     private int temp;
/* 708:    */     private File file;
/* 709:    */     private FileInputStream filein;
/* 710:    */     
/* 711:    */     public SendOutput() {}
/* 712:    */     
/* 713:    */     public void run()
/* 714:    */     {
/* 715:    */       for (;;)
/* 716:    */       {
/* 717:602 */         synchronized (this)
/* 718:    */         {
/* 719:    */           try
/* 720:    */           {
/* 721:604 */             if (this.codelen == 0)
/* 722:    */             {
/* 723:604 */               wait();
/* 724:    */             }
/* 725:    */             else
/* 726:    */             {
/* 727:605 */               int i = 0;
/* 728:605 */               if (i < this.codelen)
/* 729:    */               {
/* 730:606 */                 this.temp = ((this.code[i] & 0x7F) + (this.code[i] < 0 ? 128 : 0));
/* 731:607 */                 Terminal.this.out.write(this.code[i]);
/* 732:608 */                 Terminal.this.out.flush();i++;
/* 733:    */               }
/* 734:    */               else
/* 735:    */               {
/* 736:611 */                 this.codelen = 0;
/* 737:    */               }
/* 738:    */             }
/* 739:    */           }
/* 740:    */           catch (Exception e)
/* 741:    */           {
/* 742:612 */             System.out.println("SendOutput=>" + e);
/* 743:    */           }
/* 744:    */         }
/* 745:    */       }
/* 746:    */     }
/* 747:    */     
/* 748:    */     public void setOutput(int[] codein, int length)
/* 749:    */     {
/* 750:617 */       synchronized (this)
/* 751:    */       {
/* 752:618 */         this.code = codein;
/* 753:619 */         this.codelen = length;
/* 754:620 */         notifyAll();
/* 755:    */       }
/* 756:    */     }
/* 757:    */   }
/* 758:    */   
/* 759:    */   public class SendFileInput
/* 760:    */     implements Runnable
/* 761:    */   {
/* 762:626 */     private byte[] code = null;
/* 763:    */     private int codelen;
/* 764:    */     private File file;
/* 765:    */     private FileInputStream filein;
/* 766:    */     
/* 767:    */     public SendFileInput() {}
/* 768:    */     
/* 769:    */     public void run()
/* 770:    */     {
/* 771:    */       try
/* 772:    */       {
/* 773:632 */         Terminal.this.setLEDs(5);
/* 774:633 */         this.filein = new FileInputStream(this.file);
/* 775:634 */         this.code = new byte[512];
/* 776:635 */         while ((this.codelen = this.filein.read(this.code)) > 0)
/* 777:    */         {
/* 778:636 */           System.out.println(this.codelen);
/* 779:637 */           Terminal.this.out.write(this.code, 0, this.codelen);
/* 780:    */         }
/* 781:639 */         this.filein.close();
/* 782:640 */         this.file = null;
/* 783:641 */         Terminal.this.setLEDs(-5);
/* 784:    */       }
/* 785:    */       catch (IOException e)
/* 786:    */       {
/* 787:643 */         this.file = null;
/* 788:644 */         Terminal.this.setLEDs(-5);
/* 789:    */       }
/* 790:    */     }
/* 791:    */     
/* 792:    */     public void setFileInput(File fileinput)
/* 793:    */     {
/* 794:648 */       if (fileinput == null)
/* 795:    */       {
/* 796:649 */         if (Terminal.this.fileinth != null) {
/* 797:649 */           Terminal.this.fileinth.interrupt();
/* 798:    */         }
/* 799:    */       }
/* 800:    */       else {
/* 801:650 */         this.file = fileinput;
/* 802:    */       }
/* 803:    */     }
/* 804:    */   }
/* 805:    */   
/* 806:    */   public class ReceiveInput
/* 807:    */     implements Runnable
/* 808:    */   {
/* 809:    */     public ReceiveInput() {}
/* 810:    */     
/* 811:    */     public void run()
/* 812:    */     {
/* 813:    */       for (;;)
/* 814:    */       {
/* 815:657 */         Terminal.this.emulator.reset();
/* 816:658 */         Terminal.this.emulator.Start();
/* 817:    */       }
/* 818:    */     }
/* 819:    */   }
/* 820:    */   
/* 821:    */   public class ReceiveFileOutput
/* 822:    */     implements Runnable
/* 823:    */   {
/* 824:    */     private File file;
/* 825:665 */     public int outbyte = -1;
/* 826:    */     private BufferedOutputStream streamout;
/* 827:    */     
/* 828:    */     public ReceiveFileOutput() {}
/* 829:    */     
/* 830:    */     public void run()
/* 831:    */     {
/* 832:    */       try
/* 833:    */       {
/* 834:669 */         Terminal.this.setLEDs(5);
/* 835:670 */         this.streamout = new BufferedOutputStream(new FileOutputStream(this.file));
/* 836:671 */         while (this.file != null)
/* 837:    */         {
/* 838:    */           try
/* 839:    */           {
/* 840:673 */             if (this.outbyte >= 0)
/* 841:    */             {
/* 842:674 */               this.streamout.write(this.outbyte);
/* 843:675 */               this.outbyte = -1;
/* 844:    */             }
/* 845:    */             else
/* 846:    */             {
/* 847:677 */               Thread.sleep(1L);
/* 848:    */             }
/* 849:    */           }
/* 850:    */           catch (InterruptedException e) {}
/* 851:679 */           System.out.println("File output error" + e);
/* 852:    */         }
/* 853:681 */         this.streamout.flush();
/* 854:682 */         this.streamout.close();
/* 855:683 */         this.file = null;
/* 856:684 */         Terminal.this.setLEDs(-5);
/* 857:    */       }
/* 858:    */       catch (IOException e) {}
/* 859:    */     }
/* 860:    */     
/* 861:    */     public void setFileOutput(File fileout)
/* 862:    */     {
/* 863:689 */       this.file = fileout;
/* 864:    */     }
/* 865:    */     
/* 866:    */     public boolean sendByte(int sendbyte)
/* 867:    */     {
/* 868:    */       try
/* 869:    */       {
/* 870:    */         for (;;)
/* 871:    */         {
/* 872:694 */           if (this.outbyte == -1)
/* 873:    */           {
/* 874:695 */             this.outbyte = sendbyte;
/* 875:696 */             return true;
/* 876:    */           }
/* 877:697 */           if (this.file == null) {
/* 878:698 */             return false;
/* 879:    */           }
/* 880:700 */           Thread.sleep(1L);
/* 881:    */         }
/* 882:    */       }
/* 883:    */       catch (InterruptedException e) {}
/* 884:    */     }
/* 885:    */   }
/* 886:    */   
/* 887:    */   private static class ImageFileFilter
/* 888:    */     extends FileFilter
/* 889:    */   {
/* 890:    */     public boolean accept(File file)
/* 891:    */     {
/* 892:711 */       if (file == null) {
/* 893:712 */         return false;
/* 894:    */       }
/* 895:713 */       return (file.isDirectory()) || (file.getName().toLowerCase().endsWith(".pt")) || (file.getName().toLowerCase().endsWith(".bin"));
/* 896:    */     }
/* 897:    */     
/* 898:    */     public String getDescription()
/* 899:    */     {
/* 900:718 */       return "Reader-Punch files (*.pt,*.bin)";
/* 901:    */     }
/* 902:    */   }
/* 903:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.Terminal
 * JD-Core Version:    0.7.0.1
 */