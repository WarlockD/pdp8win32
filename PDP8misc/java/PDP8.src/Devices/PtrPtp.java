/*   1:    */ package Devices;
/*   2:    */ 
/*   3:    */ import Logic.BusRegMem;
/*   4:    */ import Logic.PC8E;
/*   5:    */ import java.awt.Color;
/*   6:    */ import java.awt.Container;
/*   7:    */ import java.awt.Dimension;
/*   8:    */ import java.awt.Font;
/*   9:    */ import java.awt.GridBagConstraints;
/*  10:    */ import java.awt.GridBagLayout;
/*  11:    */ import java.awt.event.ActionEvent;
/*  12:    */ import java.awt.event.ActionListener;
/*  13:    */ import java.awt.event.MouseEvent;
/*  14:    */ import java.awt.event.MouseListener;
/*  15:    */ import java.awt.event.WindowAdapter;
/*  16:    */ import java.awt.event.WindowEvent;
/*  17:    */ import java.beans.PropertyChangeEvent;
/*  18:    */ import java.beans.PropertyChangeListener;
/*  19:    */ import java.io.File;
/*  20:    */ import java.io.IOException;
/*  21:    */ import java.io.PrintStream;
/*  22:    */ import java.io.RandomAccessFile;
/*  23:    */ import javax.swing.AbstractButton;
/*  24:    */ import javax.swing.BorderFactory;
/*  25:    */ import javax.swing.JFileChooser;
/*  26:    */ import javax.swing.JFrame;
/*  27:    */ import javax.swing.JLabel;
/*  28:    */ import javax.swing.JMenu;
/*  29:    */ import javax.swing.JMenuItem;
/*  30:    */ import javax.swing.JPanel;
/*  31:    */ import javax.swing.JPopupMenu;
/*  32:    */ import javax.swing.border.LineBorder;
/*  33:    */ import javax.swing.border.SoftBevelBorder;
/*  34:    */ import javax.swing.filechooser.FileFilter;
/*  35:    */ 
/*  36:    */ public class PtrPtp
/*  37:    */   extends JFrame
/*  38:    */   implements MouseListener, ActionListener
/*  39:    */ {
/*  40:    */   private JLabel jLabel2;
/*  41:    */   private JLabel jLabel3;
/*  42:    */   private JLabel jLabel4;
/*  43:    */   private JLabel jLabel5;
/*  44:    */   private JPanel jPanel1;
/*  45:    */   private JPanel jPanel2;
/*  46:    */   private JPanel jPanel3;
/*  47:    */   private JPanel jPanel4;
/*  48:    */   private JPanel jPanel5;
/*  49:    */   private JPanel jPanel6;
/*  50:    */   private JPanel jPanel7;
/*  51:    */   private JPopupMenu jPopupMenu1;
/*  52:    */   public JLabel pDiodes;
/*  53:    */   public PTape pPunch1;
/*  54:    */   private PTape pRead1;
/*  55:    */   private PTape pRead2;
/*  56:    */   private DTSwitch ptpSwitch1;
/*  57:    */   private DTSwitch ptpSwitch2;
/*  58:    */   private DTSwitch ptrSwitch1;
/*  59:    */   private DTSwitch ptrSwitch2;
/*  60:    */   public PC8E pc8e;
/*  61:    */   
/*  62:    */   public PtrPtp(PC8E pc8e)
/*  63:    */   {
/*  64: 20 */     this.pc8e = pc8e;
/*  65: 21 */     setTitle("PC8E");
/*  66: 22 */     initComponents();
/*  67: 23 */     this.pRead1.startPT(this, "pRead1");
/*  68: 24 */     this.pRead2.startPT(this, "pRead2");
/*  69: 25 */     this.pPunch1.startPT(this, "pPunch1");
/*  70: 26 */     this.ptpSwitch1.addPropertyChangeListener(new PropertyChangeListener()
/*  71:    */     {
/*  72:    */       public void propertyChange(PropertyChangeEvent evt)
/*  73:    */       {
/*  74: 28 */         PtrPtp.this.prpPropertyChange(evt);
/*  75:    */       }
/*  76: 30 */     });
/*  77: 31 */     this.ptpSwitch2.addPropertyChangeListener(new PropertyChangeListener()
/*  78:    */     {
/*  79:    */       public void propertyChange(PropertyChangeEvent evt)
/*  80:    */       {
/*  81: 33 */         PtrPtp.this.prpPropertyChange(evt);
/*  82:    */       }
/*  83: 35 */     });
/*  84: 36 */     this.ptrSwitch1.addPropertyChangeListener(new PropertyChangeListener()
/*  85:    */     {
/*  86:    */       public void propertyChange(PropertyChangeEvent evt)
/*  87:    */       {
/*  88: 38 */         PtrPtp.this.prpPropertyChange(evt);
/*  89:    */       }
/*  90: 40 */     });
/*  91: 41 */     this.ptrSwitch2.addPropertyChangeListener(new PropertyChangeListener()
/*  92:    */     {
/*  93:    */       public void propertyChange(PropertyChangeEvent evt)
/*  94:    */       {
/*  95: 43 */         PtrPtp.this.prpPropertyChange(evt);
/*  96:    */       }
/*  97: 45 */     });
/*  98: 46 */     addMouseListener(this);
/*  99: 47 */     this.junit[0] = new JMenu("Reader");
/* 100: 48 */     JMenuItem mount0 = new JMenuItem("Open input reader");
/* 101: 49 */     mount0.addActionListener(this);
/* 102: 50 */     mount0.setName("0");
/* 103: 51 */     this.junit[0].add(mount0);
/* 104: 52 */     JMenuItem mount0b = new JMenuItem("Open input reader hispeed");
/* 105: 53 */     mount0b.addActionListener(this);
/* 106: 54 */     mount0b.setName("1");
/* 107: 55 */     this.junit[0].add(mount0b);
/* 108: 56 */     JMenuItem unmount0 = new JMenuItem("Remove/Close reader");
/* 109: 57 */     unmount0.addActionListener(this);
/* 110: 58 */     unmount0.setName("2");
/* 111: 59 */     this.junit[0].add(unmount0);
/* 112: 60 */     this.jPopupMenu1.add(this.junit[0]);
/* 113: 61 */     this.junit[1] = new JMenu("Punch");
/* 114: 62 */     JMenuItem mount1 = new JMenuItem("Open output punch 8-bit");
/* 115: 63 */     mount1.addActionListener(this);
/* 116: 64 */     mount1.setName("3");
/* 117: 65 */     this.junit[1].add(mount1);
/* 118: 66 */     JMenuItem mount1b = new JMenuItem("Open output punch 7-bit hispeed");
/* 119: 67 */     mount1b.addActionListener(this);
/* 120: 68 */     mount1b.setName("4");
/* 121: 69 */     this.junit[1].add(mount1b);
/* 122: 70 */     JMenuItem mount1c = new JMenuItem("Open output punch 8-bit hispeed");
/* 123: 71 */     mount1c.addActionListener(this);
/* 124: 72 */     mount1c.setName("5");
/* 125: 73 */     this.junit[1].add(mount1c);
/* 126: 74 */     JMenuItem unmount1 = new JMenuItem("Remove/Close punch");
/* 127: 75 */     unmount1.addActionListener(this);
/* 128: 76 */     unmount1.setName("6");
/* 129: 77 */     this.junit[1].add(unmount1);
/* 130: 78 */     this.jPopupMenu1.add(this.junit[1]);
/* 131:    */     
/* 132: 80 */     this.chooser = new JFileChooser();
/* 133: 81 */     this.chooser.addChoosableFileFilter(new ImageFileFilter(null));
/* 134:    */   }
/* 135:    */   
/* 136:    */   private void initComponents()
/* 137:    */   {
/* 138: 93 */     this.jPopupMenu1 = new JPopupMenu();
/* 139: 94 */     this.jPanel1 = new JPanel();
/* 140: 95 */     this.jPanel2 = new JPanel();
/* 141: 96 */     this.jPanel3 = new JPanel();
/* 142: 97 */     this.jPanel4 = new JPanel();
/* 143: 98 */     this.jLabel3 = new JLabel();
/* 144: 99 */     this.ptpSwitch1 = new DTSwitch();
/* 145:100 */     this.jLabel4 = new JLabel();
/* 146:101 */     this.ptpSwitch2 = new DTSwitch();
/* 147:102 */     this.jLabel5 = new JLabel();
/* 148:103 */     this.ptrSwitch1 = new DTSwitch();
/* 149:104 */     this.jLabel2 = new JLabel();
/* 150:105 */     this.ptrSwitch2 = new DTSwitch();
/* 151:106 */     this.jPanel6 = new JPanel();
/* 152:107 */     this.jPanel7 = new JPanel();
/* 153:108 */     this.pDiodes = new JLabel();
/* 154:109 */     this.pRead1 = new PTape();
/* 155:110 */     this.pRead2 = new PTape();
/* 156:111 */     this.jPanel5 = new JPanel();
/* 157:112 */     this.pPunch1 = new PTape();
/* 158:    */     
/* 159:114 */     addWindowListener(new WindowAdapter()
/* 160:    */     {
/* 161:    */       public void windowClosing(WindowEvent evt)
/* 162:    */       {
/* 163:116 */         PtrPtp.this.exitForm(evt);
/* 164:    */       }
/* 165:118 */     });
/* 166:119 */     getContentPane().setLayout(new GridBagLayout());
/* 167:    */     
/* 168:121 */     this.jPanel1.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(255, 255, 255), 6, true), new LineBorder(new Color(0, 0, 0), 2, true)));
/* 169:122 */     this.jPanel1.setMaximumSize(new Dimension(100, 100));
/* 170:123 */     this.jPanel1.setMinimumSize(new Dimension(300, 150));
/* 171:124 */     this.jPanel1.setPreferredSize(new Dimension(600, 300));
/* 172:125 */     this.jPanel1.setLayout(new GridBagLayout());
/* 173:    */     
/* 174:127 */     this.jPanel2.setBackground(new Color(0, 0, 0));
/* 175:128 */     this.jPanel2.setBorder(new SoftBevelBorder(0));
/* 176:129 */     this.jPanel2.setMinimumSize(new Dimension(292, 34));
/* 177:130 */     this.jPanel2.setPreferredSize(new Dimension(584, 68));
/* 178:131 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/* 179:132 */     gridBagConstraints.gridx = 0;
/* 180:133 */     gridBagConstraints.gridy = 0;
/* 181:134 */     gridBagConstraints.gridwidth = 5;
/* 182:135 */     gridBagConstraints.anchor = 17;
/* 183:136 */     this.jPanel1.add(this.jPanel2, gridBagConstraints);
/* 184:    */     
/* 185:138 */     this.jPanel3.setBackground(new Color(0, 0, 0));
/* 186:139 */     this.jPanel3.setBorder(new SoftBevelBorder(0));
/* 187:140 */     this.jPanel3.setMinimumSize(new Dimension(222, 34));
/* 188:141 */     this.jPanel3.setPreferredSize(new Dimension(444, 68));
/* 189:142 */     gridBagConstraints = new GridBagConstraints();
/* 190:143 */     gridBagConstraints.gridx = 0;
/* 191:144 */     gridBagConstraints.gridy = 4;
/* 192:145 */     gridBagConstraints.gridwidth = 3;
/* 193:146 */     gridBagConstraints.anchor = 17;
/* 194:147 */     this.jPanel1.add(this.jPanel3, gridBagConstraints);
/* 195:    */     
/* 196:149 */     this.jPanel4.setBackground(new Color(0, 0, 0));
/* 197:150 */     this.jPanel4.setBorder(new LineBorder(new Color(255, 255, 255), 3, true));
/* 198:151 */     this.jPanel4.setMinimumSize(new Dimension(42, 74));
/* 199:152 */     this.jPanel4.setPreferredSize(new Dimension(84, 148));
/* 200:153 */     this.jPanel4.setLayout(new GridBagLayout());
/* 201:    */     
/* 202:155 */     this.jLabel3.setFont(new Font("MS Sans Serif", 0, 9));
/* 203:156 */     this.jLabel3.setForeground(new Color(255, 255, 255));
/* 204:157 */     this.jLabel3.setHorizontalAlignment(0);
/* 205:158 */     this.jLabel3.setText("Off  Punch  On");
/* 206:159 */     this.jLabel3.setHorizontalTextPosition(0);
/* 207:160 */     this.jLabel3.setPreferredSize(new Dimension(70, 18));
/* 208:161 */     this.jPanel4.add(this.jLabel3, new GridBagConstraints());
/* 209:    */     
/* 210:163 */     this.ptpSwitch1.setHorizontal(true);
/* 211:164 */     this.ptpSwitch1.setMinimumSize(new Dimension(25, 9));
/* 212:165 */     this.ptpSwitch1.setName("2");
/* 213:166 */     this.ptpSwitch1.setPos(2);
/* 214:167 */     this.ptpSwitch1.setPreferredSize(new Dimension(50, 18));
/* 215:168 */     gridBagConstraints = new GridBagConstraints();
/* 216:169 */     gridBagConstraints.gridx = 0;
/* 217:170 */     gridBagConstraints.gridy = 1;
/* 218:171 */     this.jPanel4.add(this.ptpSwitch1, gridBagConstraints);
/* 219:    */     
/* 220:173 */     this.jLabel4.setFont(new Font("MS Sans Serif", 0, 8));
/* 221:174 */     this.jLabel4.setForeground(new Color(255, 255, 255));
/* 222:175 */     this.jLabel4.setHorizontalAlignment(11);
/* 223:176 */     this.jLabel4.setText("Feed");
/* 224:177 */     this.jLabel4.setPreferredSize(new Dimension(70, 12));
/* 225:178 */     gridBagConstraints = new GridBagConstraints();
/* 226:179 */     gridBagConstraints.gridx = 0;
/* 227:180 */     gridBagConstraints.gridy = 2;
/* 228:181 */     this.jPanel4.add(this.jLabel4, gridBagConstraints);
/* 229:    */     
/* 230:183 */     this.ptpSwitch2.setHorizontal(true);
/* 231:184 */     this.ptpSwitch2.setMinimumSize(new Dimension(25, 9));
/* 232:185 */     this.ptpSwitch2.setMomentary(true);
/* 233:186 */     this.ptpSwitch2.setName("3");
/* 234:187 */     this.ptpSwitch2.setPos(2);
/* 235:188 */     this.ptpSwitch2.setPreferredSize(new Dimension(50, 18));
/* 236:189 */     gridBagConstraints = new GridBagConstraints();
/* 237:190 */     gridBagConstraints.gridx = 0;
/* 238:191 */     gridBagConstraints.gridy = 3;
/* 239:192 */     this.jPanel4.add(this.ptpSwitch2, gridBagConstraints);
/* 240:    */     
/* 241:194 */     this.jLabel5.setBackground(new Color(0, 0, 0));
/* 242:195 */     this.jLabel5.setFont(new Font("MS Sans Serif", 0, 9));
/* 243:196 */     this.jLabel5.setForeground(new Color(255, 255, 255));
/* 244:197 */     this.jLabel5.setHorizontalAlignment(0);
/* 245:198 */     this.jLabel5.setText("Off  Reader  On");
/* 246:199 */     this.jLabel5.setMinimumSize(new Dimension(42, 6));
/* 247:200 */     this.jLabel5.setOpaque(true);
/* 248:201 */     this.jLabel5.setPreferredSize(new Dimension(70, 18));
/* 249:202 */     gridBagConstraints = new GridBagConstraints();
/* 250:203 */     gridBagConstraints.gridx = 0;
/* 251:204 */     gridBagConstraints.gridy = 4;
/* 252:205 */     this.jPanel4.add(this.jLabel5, gridBagConstraints);
/* 253:    */     
/* 254:207 */     this.ptrSwitch1.setHorizontal(true);
/* 255:208 */     this.ptrSwitch1.setMinimumSize(new Dimension(25, 9));
/* 256:209 */     this.ptrSwitch1.setName("0");
/* 257:210 */     this.ptrSwitch1.setPos(2);
/* 258:211 */     this.ptrSwitch1.setPreferredSize(new Dimension(50, 18));
/* 259:212 */     gridBagConstraints = new GridBagConstraints();
/* 260:213 */     gridBagConstraints.gridx = 0;
/* 261:214 */     gridBagConstraints.gridy = 5;
/* 262:215 */     this.jPanel4.add(this.ptrSwitch1, gridBagConstraints);
/* 263:    */     
/* 264:217 */     this.jLabel2.setFont(new Font("MS Sans Serif", 0, 8));
/* 265:218 */     this.jLabel2.setForeground(new Color(255, 255, 255));
/* 266:219 */     this.jLabel2.setHorizontalAlignment(11);
/* 267:220 */     this.jLabel2.setText("Feed");
/* 268:221 */     this.jLabel2.setPreferredSize(new Dimension(76, 12));
/* 269:222 */     gridBagConstraints = new GridBagConstraints();
/* 270:223 */     gridBagConstraints.gridx = 0;
/* 271:224 */     gridBagConstraints.gridy = 6;
/* 272:225 */     this.jPanel4.add(this.jLabel2, gridBagConstraints);
/* 273:    */     
/* 274:227 */     this.ptrSwitch2.setHorizontal(true);
/* 275:228 */     this.ptrSwitch2.setMinimumSize(new Dimension(25, 9));
/* 276:229 */     this.ptrSwitch2.setMomentary(true);
/* 277:230 */     this.ptrSwitch2.setName("1");
/* 278:231 */     this.ptrSwitch2.setPos(2);
/* 279:232 */     this.ptrSwitch2.setPreferredSize(new Dimension(50, 18));
/* 280:233 */     gridBagConstraints = new GridBagConstraints();
/* 281:234 */     gridBagConstraints.gridx = 0;
/* 282:235 */     gridBagConstraints.gridy = 7;
/* 283:236 */     this.jPanel4.add(this.ptrSwitch2, gridBagConstraints);
/* 284:    */     
/* 285:238 */     gridBagConstraints = new GridBagConstraints();
/* 286:239 */     gridBagConstraints.gridx = 4;
/* 287:240 */     gridBagConstraints.gridy = 1;
/* 288:241 */     gridBagConstraints.gridheight = 3;
/* 289:242 */     gridBagConstraints.anchor = 17;
/* 290:243 */     this.jPanel1.add(this.jPanel4, gridBagConstraints);
/* 291:    */     
/* 292:245 */     this.jPanel6.setBackground(new Color(51, 51, 51));
/* 293:246 */     this.jPanel6.setBorder(new SoftBevelBorder(0));
/* 294:247 */     this.jPanel6.setMinimumSize(new Dimension(74, 15));
/* 295:248 */     this.jPanel6.setPreferredSize(new Dimension(148, 30));
/* 296:249 */     gridBagConstraints = new GridBagConstraints();
/* 297:250 */     gridBagConstraints.gridx = 1;
/* 298:251 */     gridBagConstraints.gridy = 1;
/* 299:252 */     gridBagConstraints.anchor = 11;
/* 300:253 */     this.jPanel1.add(this.jPanel6, gridBagConstraints);
/* 301:    */     
/* 302:255 */     this.jPanel7.setBackground(new Color(51, 51, 51));
/* 303:256 */     this.jPanel7.setBorder(new SoftBevelBorder(0));
/* 304:257 */     this.jPanel7.setMinimumSize(new Dimension(74, 55));
/* 305:258 */     this.jPanel7.setPreferredSize(new Dimension(148, 106));
/* 306:259 */     gridBagConstraints = new GridBagConstraints();
/* 307:260 */     gridBagConstraints.gridx = 1;
/* 308:261 */     gridBagConstraints.gridy = 3;
/* 309:262 */     gridBagConstraints.anchor = 15;
/* 310:263 */     this.jPanel1.add(this.jPanel7, gridBagConstraints);
/* 311:    */     
/* 312:265 */     this.pDiodes.setBackground(new Color(102, 102, 102));
/* 313:266 */     this.pDiodes.setBorder(new SoftBevelBorder(1));
/* 314:267 */     this.pDiodes.setMinimumSize(new Dimension(74, 6));
/* 315:268 */     this.pDiodes.setOpaque(true);
/* 316:269 */     this.pDiodes.setPreferredSize(new Dimension(148, 12));
/* 317:270 */     gridBagConstraints = new GridBagConstraints();
/* 318:271 */     gridBagConstraints.gridx = 1;
/* 319:272 */     gridBagConstraints.gridy = 2;
/* 320:273 */     this.jPanel1.add(this.pDiodes, gridBagConstraints);
/* 321:    */     
/* 322:275 */     this.pRead1.setBorder(BorderFactory.createEtchedBorder());
/* 323:276 */     gridBagConstraints = new GridBagConstraints();
/* 324:277 */     gridBagConstraints.gridx = 0;
/* 325:278 */     gridBagConstraints.gridy = 1;
/* 326:279 */     gridBagConstraints.gridheight = 3;
/* 327:280 */     this.jPanel1.add(this.pRead1, gridBagConstraints);
/* 328:    */     
/* 329:282 */     this.pRead2.setBorder(BorderFactory.createEtchedBorder());
/* 330:283 */     this.pRead2.setRight(true);
/* 331:284 */     gridBagConstraints = new GridBagConstraints();
/* 332:285 */     gridBagConstraints.gridx = 2;
/* 333:286 */     gridBagConstraints.gridy = 1;
/* 334:287 */     gridBagConstraints.gridheight = 3;
/* 335:288 */     this.jPanel1.add(this.pRead2, gridBagConstraints);
/* 336:    */     
/* 337:290 */     this.jPanel5.setBackground(new Color(0, 0, 0));
/* 338:291 */     this.jPanel5.setBorder(new SoftBevelBorder(0));
/* 339:292 */     this.jPanel5.setMinimumSize(new Dimension(42, 34));
/* 340:293 */     this.jPanel5.setPreferredSize(new Dimension(84, 68));
/* 341:294 */     gridBagConstraints = new GridBagConstraints();
/* 342:295 */     gridBagConstraints.gridx = 4;
/* 343:296 */     gridBagConstraints.gridy = 4;
/* 344:297 */     this.jPanel1.add(this.jPanel5, gridBagConstraints);
/* 345:    */     
/* 346:299 */     this.pPunch1.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
/* 347:300 */     this.pPunch1.setMinimumSize(new Dimension(28, 110));
/* 348:301 */     this.pPunch1.setPreferredSize(new Dimension(56, 216));
/* 349:302 */     this.pPunch1.setRight(true);
/* 350:303 */     this.pPunch1.setUnit(1);
/* 351:304 */     gridBagConstraints = new GridBagConstraints();
/* 352:305 */     gridBagConstraints.gridx = 3;
/* 353:306 */     gridBagConstraints.gridy = 1;
/* 354:307 */     gridBagConstraints.gridheight = 4;
/* 355:308 */     this.jPanel1.add(this.pPunch1, gridBagConstraints);
/* 356:    */     
/* 357:310 */     getContentPane().add(this.jPanel1, new GridBagConstraints());
/* 358:    */     
/* 359:312 */     pack();
/* 360:    */   }
/* 361:    */   
/* 362:    */   private void exitForm(WindowEvent evt)
/* 363:    */   {
/* 364:317 */     this.pc8e.data.CloseAllDevs();
/* 365:    */   }
/* 366:    */   
/* 367:    */   private void prpPropertyChange(PropertyChangeEvent evt)
/* 368:    */   {
/* 369:321 */     Integer objswr = (Integer)evt.getNewValue();
/* 370:322 */     int val = objswr.intValue();
/* 371:323 */     int mask = Integer.parseInt(evt.getPropertyName());
/* 372:324 */     int unit = mask >> 1;
/* 373:325 */     switch (mask & 0x1)
/* 374:    */     {
/* 375:    */     case 0: 
/* 376:326 */       selTape(unit, val); break;
/* 377:    */     case 1: 
/* 378:327 */       moveTape(unit, val);
/* 379:    */     }
/* 380:    */   }
/* 381:    */   
/* 382:    */   private void selTape(int unit, int val)
/* 383:    */   {
/* 384:332 */     if (val == 1)
/* 385:    */     {
/* 386:333 */       this.pc8e.sel[unit] = true;
/* 387:334 */       this.local[unit] = false;
/* 388:    */     }
/* 389:    */     else
/* 390:    */     {
/* 391:337 */       this.pc8e.sel[unit] = false;
/* 392:338 */       if (val == 2) {
/* 393:338 */         this.local[unit] = true;
/* 394:    */       } else {
/* 395:339 */         this.local[unit] = false;
/* 396:    */       }
/* 397:    */     }
/* 398:    */   }
/* 399:    */   
/* 400:    */   private void moveTape(int unit, int val)
/* 401:    */   {
/* 402:344 */     if (this.local[unit] == 1) {
/* 403:345 */       switch (val)
/* 404:    */       {
/* 405:    */       case 1: 
/* 406:346 */         this.direction[unit] = 1; break;
/* 407:    */       case 2: 
/* 408:348 */         this.direction[unit] = 0;
/* 409:    */       }
/* 410:    */     }
/* 411:    */   }
/* 412:    */   
/* 413:    */   public void closeTape(int unit)
/* 414:    */   {
/* 415:354 */     if (this.pc8e.tape[unit] != null)
/* 416:    */     {
/* 417:    */       try
/* 418:    */       {
/* 419:356 */         this.pc8e.tape[unit].close();
/* 420:    */       }
/* 421:    */       catch (IOException e) {}
/* 422:360 */       this.pc8e.tape[unit] = null;
/* 423:361 */       this.pc8e.havetape[unit] = false;
/* 424:362 */       this.tapesize[unit] = 0;
/* 425:363 */       this.pc8e.line[unit] = 0;
/* 426:364 */       if (unit == 0) {
/* 427:364 */         this.junit[unit].setText("Reader");
/* 428:    */       } else {
/* 429:365 */         this.junit[unit].setText("Punch");
/* 430:    */       }
/* 431:    */     }
/* 432:    */   }
/* 433:    */   
/* 434:    */   public void mouseClicked(MouseEvent e)
/* 435:    */   {
/* 436:370 */     if (e.isPopupTrigger()) {
/* 437:371 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/* 438:    */     }
/* 439:    */   }
/* 440:    */   
/* 441:    */   public void mouseEntered(MouseEvent e) {}
/* 442:    */   
/* 443:    */   public void mouseExited(MouseEvent e) {}
/* 444:    */   
/* 445:    */   public void mousePressed(MouseEvent e)
/* 446:    */   {
/* 447:382 */     if (e.isPopupTrigger()) {
/* 448:383 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/* 449:    */     }
/* 450:    */   }
/* 451:    */   
/* 452:    */   public void mouseReleased(MouseEvent e)
/* 453:    */   {
/* 454:388 */     if (e.isPopupTrigger()) {
/* 455:389 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/* 456:    */     }
/* 457:    */   }
/* 458:    */   
/* 459:    */   public void actionPerformed(ActionEvent e)
/* 460:    */   {
/* 461:394 */     AbstractButton x = (AbstractButton)e.getSource();
/* 462:395 */     Integer val = new Integer(Integer.parseInt(x.getName()));
/* 463:396 */     this.pc8e.sevenbit = false;
/* 464:397 */     this.pc8e.punhispeed = false;
/* 465:398 */     this.pc8e.rdrhispeed = false;
/* 466:399 */     switch (val.intValue() & 0x7)
/* 467:    */     {
/* 468:    */     case 0: 
/* 469:400 */       this.pc8e.rdrhispeed = false;openTape(0); break;
/* 470:    */     case 1: 
/* 471:401 */       this.pc8e.rdrhispeed = true;openTape(0); break;
/* 472:    */     case 2: 
/* 473:402 */       closeTape(0); break;
/* 474:    */     case 3: 
/* 475:403 */       this.pc8e.sevenbit = false;this.pc8e.punhispeed = false;openTape(1); break;
/* 476:    */     case 4: 
/* 477:404 */       this.pc8e.sevenbit = true;this.pc8e.punhispeed = true;openTape(1); break;
/* 478:    */     case 5: 
/* 479:405 */       this.pc8e.sevenbit = false;this.pc8e.punhispeed = true;openTape(1); break;
/* 480:    */     case 6: 
/* 481:406 */       closeTape(1);
/* 482:    */     }
/* 483:408 */     repaint();
/* 484:    */   }
/* 485:    */   
/* 486:    */   private void openTape(int unit)
/* 487:    */   {
/* 488:413 */     String dfile = this.pc8e.data.getProp("PtrPtp-" + unit);
/* 489:414 */     if (dfile != null) {
/* 490:414 */       this.chooser.setSelectedFile(new File(dfile));
/* 491:    */     }
/* 492:    */     int option;
/* 493:    */     int option;
/* 494:415 */     if (unit == 0) {
/* 495:415 */       option = this.chooser.showOpenDialog(this);
/* 496:    */     } else {
/* 497:416 */       option = this.chooser.showSaveDialog(this);
/* 498:    */     }
/* 499:417 */     if (option == 0)
/* 500:    */     {
/* 501:418 */       File file = this.chooser.getSelectedFile();
/* 502:419 */       if (file != null)
/* 503:    */       {
/* 504:420 */         closeTape(unit);
/* 505:    */         try
/* 506:    */         {
/* 507:422 */           if ((file.isFile()) && 
/* 508:423 */             (unit == 0)) {
/* 509:424 */             if (file.canRead())
/* 510:    */             {
/* 511:425 */               this.pc8e.tape[unit] = new RandomAccessFile(file, "r");
/* 512:426 */               this.tapesize[unit] = ((int)file.length());
/* 513:427 */               this.pc8e.line[unit] = 0;
/* 514:428 */               this.pc8e.havetape[unit] = true;
/* 515:429 */               this.pc8e.fetching = false;
/* 516:430 */               this.junit[unit].setText("Reader: " + file.getName());
/* 517:431 */               this.pc8e.data.setProp("PtrPtp-" + unit, file.getCanonicalPath());
/* 518:    */             }
/* 519:    */             else
/* 520:    */             {
/* 521:432 */               System.out.println("File seems to be busy!");
/* 522:    */             }
/* 523:    */           }
/* 524:435 */           if (unit == 1) {
/* 525:436 */             if (file.length() == 0L)
/* 526:    */             {
/* 527:437 */               this.pc8e.tape[unit] = new RandomAccessFile(file, "rw");
/* 528:438 */               this.tapesize[unit] = 10000;
/* 529:439 */               this.pc8e.line[unit] = 0;
/* 530:440 */               this.pc8e.havetape[unit] = true;
/* 531:441 */               this.junit[unit].setText("Punch: " + file.getName());
/* 532:442 */               this.pc8e.data.setProp("PtrPtp-" + unit, file.getCanonicalPath());
/* 533:    */             }
/* 534:    */             else
/* 535:    */             {
/* 536:443 */               System.out.println("File seems to be busy!");
/* 537:    */             }
/* 538:    */           }
/* 539:    */         }
/* 540:    */         catch (IOException e)
/* 541:    */         {
/* 542:447 */           System.out.println(e);
/* 543:    */         }
/* 544:    */       }
/* 545:    */     }
/* 546:    */     else
/* 547:    */     {
/* 548:450 */       System.out.println("No file selected");
/* 549:    */     }
/* 550:    */   }
/* 551:    */   
/* 552:    */   private static class ImageFileFilter
/* 553:    */     extends FileFilter
/* 554:    */   {
/* 555:    */     public boolean accept(File file)
/* 556:    */     {
/* 557:457 */       if (file == null) {
/* 558:458 */         return false;
/* 559:    */       }
/* 560:459 */       return (file.isDirectory()) || (file.getName().toLowerCase().endsWith(".pt")) || (file.getName().toLowerCase().endsWith(".bin"));
/* 561:    */     }
/* 562:    */     
/* 563:    */     public String getDescription()
/* 564:    */     {
/* 565:463 */       return "Paper tapes (*.pt, *.bin)";
/* 566:    */     }
/* 567:    */   }
/* 568:    */   
/* 569:491 */   public int[] tapesize = { 0, 0 };
/* 570:492 */   public boolean[] local = { true, true };
/* 571:493 */   public int[] direction = { 0, 0 };
/* 572:494 */   JMenu[] junit = { null, null };
/* 573:    */   JFileChooser chooser;
/* 574:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.PtrPtp
 * JD-Core Version:    0.7.0.1
 */