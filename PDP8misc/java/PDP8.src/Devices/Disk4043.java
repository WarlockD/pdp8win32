/*   1:    */ package Devices;
/*   2:    */ 
/*   3:    */ import Logic.BusRegMem;
/*   4:    */ import Logic.Constants;
/*   5:    */ import Logic.SI3040;
/*   6:    */ import java.awt.Color;
/*   7:    */ import java.awt.Container;
/*   8:    */ import java.awt.Cursor;
/*   9:    */ import java.awt.Dimension;
/*  10:    */ import java.awt.GridBagConstraints;
/*  11:    */ import java.awt.GridBagLayout;
/*  12:    */ import java.awt.event.ActionEvent;
/*  13:    */ import java.awt.event.ActionListener;
/*  14:    */ import java.awt.event.MouseEvent;
/*  15:    */ import java.awt.event.MouseListener;
/*  16:    */ import java.awt.event.WindowAdapter;
/*  17:    */ import java.awt.event.WindowEvent;
/*  18:    */ import java.io.File;
/*  19:    */ import java.io.IOException;
/*  20:    */ import java.io.PrintStream;
/*  21:    */ import java.io.RandomAccessFile;
/*  22:    */ import javax.swing.AbstractButton;
/*  23:    */ import javax.swing.ButtonGroup;
/*  24:    */ import javax.swing.JFileChooser;
/*  25:    */ import javax.swing.JFrame;
/*  26:    */ import javax.swing.JMenu;
/*  27:    */ import javax.swing.JMenuItem;
/*  28:    */ import javax.swing.JOptionPane;
/*  29:    */ import javax.swing.JPanel;
/*  30:    */ import javax.swing.JPopupMenu;
/*  31:    */ import javax.swing.JRadioButtonMenuItem;
/*  32:    */ import javax.swing.border.LineBorder;
/*  33:    */ import javax.swing.filechooser.FileFilter;
/*  34:    */ 
/*  35:    */ public class Disk4043
/*  36:    */   extends JFrame
/*  37:    */   implements Constants, MouseListener, ActionListener
/*  38:    */ {
/*  39:    */   public DiskUnit diskUnit0;
/*  40:    */   public DiskUnit diskUnit1;
/*  41:    */   private DiskUnit diskUnit2;
/*  42:    */   private DiskUnit diskUnit3;
/*  43:    */   private JPanel jPanel2;
/*  44:    */   private JPopupMenu jPopupMenu1;
/*  45:    */   public SI3040 si3040;
/*  46:    */   
/*  47:    */   public Disk4043(SI3040 si3040)
/*  48:    */   {
/*  49: 23 */     this.si3040 = si3040;
/*  50: 24 */     setTitle("SI3040-M4043");
/*  51: 25 */     initComponents();
/*  52: 26 */     this.diskUnit0.startUnit(this, "unit0");
/*  53: 27 */     this.diskUnit1.startUnit(this, "unit1");
/*  54: 28 */     this.diskUnit2.startUnit(this, "unit2");
/*  55: 29 */     this.diskUnit3.startUnit(this, "unit3");
/*  56:    */     
/*  57: 31 */     addMouseListener(this);
/*  58: 32 */     this.junit[0][0] = new JMenu("Unit 0 fixed");
/*  59: 33 */     JMenuItem mount0 = new JMenuItem("Mount");
/*  60: 34 */     mount0.addActionListener(this);
/*  61: 35 */     mount0.setName("0");
/*  62: 36 */     this.junit[0][0].add(mount0);
/*  63: 37 */     JMenuItem unmount0 = new JMenuItem("Unmount");
/*  64: 38 */     unmount0.addActionListener(this);
/*  65: 39 */     unmount0.setName("1");
/*  66: 40 */     this.junit[0][0].add(unmount0);
/*  67: 41 */     this.jPopupMenu1.add(this.junit[0][0]);
/*  68: 42 */     this.junit[0][1] = new JMenu("Unit 0 rem");
/*  69: 43 */     JMenuItem mount0r = new JMenuItem("Mount");
/*  70: 44 */     mount0r.addActionListener(this);
/*  71: 45 */     mount0r.setName("2");
/*  72: 46 */     this.junit[0][1].add(mount0r);
/*  73: 47 */     JMenuItem unmount0r = new JMenuItem("Unmount");
/*  74: 48 */     unmount0r.addActionListener(this);
/*  75: 49 */     unmount0r.setName("3");
/*  76: 50 */     this.junit[0][1].add(unmount0r);
/*  77: 51 */     this.jPopupMenu1.add(this.junit[0][1]);
/*  78: 52 */     this.junit[1][0] = new JMenu("Unit 1 fixed");
/*  79: 53 */     JMenuItem mount1 = new JMenuItem("Mount");
/*  80: 54 */     mount1.addActionListener(this);
/*  81: 55 */     mount1.setName("4");
/*  82: 56 */     this.junit[1][0].add(mount1);
/*  83: 57 */     JMenuItem unmount1 = new JMenuItem("Unmount");
/*  84: 58 */     unmount1.addActionListener(this);
/*  85: 59 */     unmount1.setName("5");
/*  86: 60 */     this.junit[1][0].add(unmount1);
/*  87: 61 */     this.jPopupMenu1.add(this.junit[1][0]);
/*  88: 62 */     this.junit[1][1] = new JMenu("Unit 1 rem");
/*  89: 63 */     JMenuItem mount1r = new JMenuItem("Mount");
/*  90: 64 */     mount1r.addActionListener(this);
/*  91: 65 */     mount1r.setName("6");
/*  92: 66 */     this.junit[1][1].add(mount1r);
/*  93: 67 */     JMenuItem unmount1r = new JMenuItem("Unmount");
/*  94: 68 */     unmount1r.addActionListener(this);
/*  95: 69 */     unmount1r.setName("7");
/*  96: 70 */     this.junit[1][1].add(unmount1r);
/*  97: 71 */     this.jPopupMenu1.add(this.junit[1][1]);
/*  98: 72 */     this.jPopupMenu1.addSeparator();
/*  99: 73 */     ButtonGroup group = new ButtonGroup();
/* 100: 74 */     JRadioButtonMenuItem nofmt = new JRadioButtonMenuItem("Data");
/* 101: 75 */     nofmt.addActionListener(this);
/* 102: 76 */     nofmt.setName("8");
/* 103: 77 */     nofmt.setSelected(true);
/* 104: 78 */     group.add(nofmt);
/* 105: 79 */     this.jPopupMenu1.add(nofmt);
/* 106: 80 */     JRadioButtonMenuItem fmt = new JRadioButtonMenuItem("Format");
/* 107: 81 */     fmt.addActionListener(this);
/* 108: 82 */     fmt.setName("9");
/* 109: 83 */     group.add(fmt);
/* 110: 84 */     this.jPopupMenu1.add(fmt);
/* 111:    */     
/* 112: 86 */     this.chooser = new JFileChooser();
/* 113: 87 */     this.chooser.addChoosableFileFilter(new ImageFileFilter(null));
/* 114: 89 */     for (int u = 0; u < 2; u++) {
/* 115: 90 */       for (int r = 0; r < 2; r++) {
/* 116: 91 */         if (si3040.data.getProp("Disk4043-" + u + "-" + r) != null) {
/* 117: 92 */           openDisk(u, r);
/* 118:    */         }
/* 119:    */       }
/* 120:    */     }
/* 121:    */   }
/* 122:    */   
/* 123:    */   private void initComponents()
/* 124:    */   {
/* 125:107 */     this.jPopupMenu1 = new JPopupMenu();
/* 126:108 */     this.jPanel2 = new JPanel();
/* 127:109 */     this.diskUnit0 = new DiskUnit();
/* 128:110 */     this.diskUnit1 = new DiskUnit();
/* 129:111 */     this.diskUnit2 = new DiskUnit();
/* 130:112 */     this.diskUnit3 = new DiskUnit();
/* 131:    */     
/* 132:114 */     setCursor(new Cursor(0));
/* 133:115 */     addWindowListener(new WindowAdapter()
/* 134:    */     {
/* 135:    */       public void windowClosing(WindowEvent evt)
/* 136:    */       {
/* 137:117 */         Disk4043.this.exitForm(evt);
/* 138:    */       }
/* 139:119 */     });
/* 140:120 */     getContentPane().setLayout(new GridBagLayout());
/* 141:    */     
/* 142:122 */     this.jPanel2.setBackground(new Color(0, 0, 0));
/* 143:123 */     this.jPanel2.setBorder(new LineBorder(new Color(255, 255, 255), 8, true));
/* 144:124 */     this.jPanel2.setMaximumSize(new Dimension(316, 180));
/* 145:125 */     this.jPanel2.setMinimumSize(new Dimension(166, 90));
/* 146:126 */     this.jPanel2.setPreferredSize(new Dimension(316, 180));
/* 147:127 */     this.jPanel2.setLayout(new GridBagLayout());
/* 148:    */     
/* 149:129 */     this.diskUnit0.setMaximumSize(new Dimension(300, 60));
/* 150:130 */     this.jPanel2.add(this.diskUnit0, new GridBagConstraints());
/* 151:    */     
/* 152:132 */     this.diskUnit1.setMaximumSize(new Dimension(300, 60));
/* 153:133 */     this.diskUnit1.setUnit(1);
/* 154:134 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/* 155:135 */     gridBagConstraints.gridx = 0;
/* 156:136 */     gridBagConstraints.gridy = 1;
/* 157:137 */     this.jPanel2.add(this.diskUnit1, gridBagConstraints);
/* 158:    */     
/* 159:139 */     this.diskUnit2.setMaximumSize(new Dimension(300, 60));
/* 160:140 */     this.diskUnit2.setUnit(2);
/* 161:141 */     gridBagConstraints = new GridBagConstraints();
/* 162:142 */     gridBagConstraints.gridx = 0;
/* 163:143 */     gridBagConstraints.gridy = 2;
/* 164:144 */     this.jPanel2.add(this.diskUnit2, gridBagConstraints);
/* 165:    */     
/* 166:146 */     this.diskUnit3.setMaximumSize(new Dimension(300, 60));
/* 167:147 */     this.diskUnit3.setUnit(3);
/* 168:148 */     gridBagConstraints = new GridBagConstraints();
/* 169:149 */     gridBagConstraints.gridx = 0;
/* 170:150 */     gridBagConstraints.gridy = 3;
/* 171:151 */     this.jPanel2.add(this.diskUnit3, gridBagConstraints);
/* 172:    */     
/* 173:153 */     getContentPane().add(this.jPanel2, new GridBagConstraints());
/* 174:    */     
/* 175:155 */     pack();
/* 176:    */   }
/* 177:    */   
/* 178:    */   private void exitForm(WindowEvent evt)
/* 179:    */   {
/* 180:160 */     this.si3040.data.CloseAllDevs();
/* 181:    */   }
/* 182:    */   
/* 183:    */   public void mouseClicked(MouseEvent e)
/* 184:    */   {
/* 185:166 */     if (e.isPopupTrigger()) {
/* 186:167 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void mouseEntered(MouseEvent e) {}
/* 191:    */   
/* 192:    */   public void mouseExited(MouseEvent e) {}
/* 193:    */   
/* 194:    */   public void mousePressed(MouseEvent e)
/* 195:    */   {
/* 196:178 */     if (e.isPopupTrigger()) {
/* 197:179 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/* 198:    */     }
/* 199:    */   }
/* 200:    */   
/* 201:    */   public void mouseReleased(MouseEvent e)
/* 202:    */   {
/* 203:184 */     if (e.isPopupTrigger()) {
/* 204:185 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/* 205:    */     }
/* 206:    */   }
/* 207:    */   
/* 208:    */   public void actionPerformed(ActionEvent e)
/* 209:    */   {
/* 210:190 */     AbstractButton x = (AbstractButton)e.getSource();
/* 211:191 */     Integer val = new Integer(Integer.parseInt(x.getName()));
/* 212:192 */     switch (val.intValue())
/* 213:    */     {
/* 214:    */     case 0: 
/* 215:193 */       openDisk(0, 0); break;
/* 216:    */     case 1: 
/* 217:194 */       closeDisk(0, 0, false); break;
/* 218:    */     case 2: 
/* 219:195 */       openDisk(0, 1); break;
/* 220:    */     case 3: 
/* 221:196 */       closeDisk(0, 1, false); break;
/* 222:    */     case 4: 
/* 223:197 */       openDisk(1, 0); break;
/* 224:    */     case 5: 
/* 225:198 */       closeDisk(1, 0, false); break;
/* 226:    */     case 6: 
/* 227:199 */       openDisk(1, 1); break;
/* 228:    */     case 7: 
/* 229:200 */       closeDisk(1, 1, false); break;
/* 230:    */     case 8: 
/* 231:201 */       this.si3040.fmton = false; break;
/* 232:    */     case 9: 
/* 233:202 */       this.si3040.fmton = true;
/* 234:    */     }
/* 235:204 */     repaint();
/* 236:    */   }
/* 237:    */   
/* 238:    */   private void openDisk(int unit, int removable)
/* 239:    */   {
/* 240:208 */     File file = null;
/* 241:209 */     File rkfile = null;
/* 242:210 */     boolean convert = false;
/* 243:211 */     String dfile = this.si3040.data.getProp("Disk4043-" + unit + "-" + removable);
/* 244:212 */     if (dfile == null)
/* 245:    */     {
/* 246:213 */       int option = this.chooser.showOpenDialog(this);
/* 247:214 */       if (option == 0) {
/* 248:215 */         file = this.chooser.getSelectedFile();
/* 249:    */       }
/* 250:    */     }
/* 251:    */     else
/* 252:    */     {
/* 253:217 */       file = new File(dfile);
/* 254:    */     }
/* 255:218 */     if (file.getName().endsWith(".rk05"))
/* 256:    */     {
/* 257:219 */       convert = true;
/* 258:    */       try
/* 259:    */       {
/* 260:221 */         String rkfname = file.getCanonicalPath();
/* 261:222 */         rkfile = new File(rkfname);
/* 262:223 */         int ind = rkfname.lastIndexOf(".rk05");
/* 263:224 */         dfile = rkfname.substring(0, ind) + ".m43";
/* 264:225 */         file = new File(dfile);
/* 265:    */       }
/* 266:    */       catch (IOException e)
/* 267:    */       {
/* 268:227 */         System.out.println(e);
/* 269:    */       }
/* 270:    */     }
/* 271:230 */     if (file != null)
/* 272:    */     {
/* 273:231 */       closeDisk(unit, removable, true);
/* 274:    */       try
/* 275:    */       {
/* 276:233 */         if (file.isFile())
/* 277:    */         {
/* 278:234 */           if (file.canWrite())
/* 279:    */           {
/* 280:235 */             this.si3040.disk[unit][removable] = new RandomAccessFile(file, "rw");
/* 281:    */           }
/* 282:    */           else
/* 283:    */           {
/* 284:237 */             System.out.println("File seems to be busy!");
/* 285:238 */             JOptionPane.showMessageDialog(this, file + " is not accessible - read-only?!", "Disk 4043", 2);
/* 286:    */           }
/* 287:    */         }
/* 288:    */         else
/* 289:    */         {
/* 290:245 */           JOptionPane.showMessageDialog(this, file + " is not found!", "Disk 4043", 2);
/* 291:    */           
/* 292:    */ 
/* 293:    */ 
/* 294:249 */           return;
/* 295:    */         }
/* 296:251 */         this.si3040.sel[unit][removable] = 1;
/* 297:252 */         String rtex = " fixed: ";
/* 298:252 */         if (removable > 0) {
/* 299:252 */           rtex = " rem: ";
/* 300:    */         }
/* 301:253 */         this.junit[unit][removable].setText("Unit " + unit + rtex + file.getName());
/* 302:254 */         this.si3040.data.setProp("Disk4043-" + unit + "-" + removable, file.getCanonicalPath());
/* 303:    */       }
/* 304:    */       catch (IOException e)
/* 305:    */       {
/* 306:256 */         System.out.println(e);
/* 307:    */       }
/* 308:258 */       if (this.si3040.sel[unit][removable] != 0) {
/* 309:259 */         if (file.length() == 0L)
/* 310:    */         {
/* 311:260 */           if (convert)
/* 312:    */           {
/* 313:    */             try
/* 314:    */             {
/* 315:262 */               this.si3040.disk[unit][removable] = new RandomAccessFile(file, "rw");
/* 316:263 */               RandomAccessFile rkdisk = new RandomAccessFile(rkfile, "r");
/* 317:264 */               for (int u = 0; u < 2; u++) {
/* 318:265 */                 for (int block = 0; block < 204; block++) {
/* 319:266 */                   for (int sect = 0; sect < 16; sect++)
/* 320:    */                   {
/* 321:267 */                     rkdisk.seek((u * 203 + block << 4 | sect) * 384);
/* 322:268 */                     this.si3040.disk[unit][removable].seek(28 + (u * 204 + block << 4 | sect) * 417);
/* 323:269 */                     this.si3040.disk[unit][removable].writeInt(134217728 + (u * 204 + block << 4));
/* 324:270 */                     this.si3040.disk[unit][removable].seek(33 + (u * 204 + block << 4 | sect) * 417);
/* 325:271 */                     for (int b = 0; b < 384; b++) {
/* 326:272 */                       this.si3040.disk[unit][removable].write(rkdisk.read());
/* 327:    */                     }
/* 328:    */                   }
/* 329:    */                 }
/* 330:    */               }
/* 331:277 */               rkdisk.close();
/* 332:    */             }
/* 333:    */             catch (IOException e)
/* 334:    */             {
/* 335:279 */               System.out.println(e);
/* 336:    */             }
/* 337:281 */             System.out.println("rk05 file transformed to m43 format!");
/* 338:    */           }
/* 339:283 */           else if (this.si3040.fmton)
/* 340:    */           {
/* 341:    */             try
/* 342:    */             {
/* 343:285 */               this.si3040.disk[unit][removable] = new RandomAccessFile(file, "rw");
/* 344:286 */               for (int block = 0; block < 408; block++) {
/* 345:287 */                 for (int sect = 0; sect < 16; sect++)
/* 346:    */                 {
/* 347:288 */                   this.si3040.disk[unit][removable].seek(28 + (block << 4 | sect) * 417);
/* 348:289 */                   this.si3040.disk[unit][removable].writeInt(134217728 + (block << 4));
/* 349:    */                 }
/* 350:    */               }
/* 351:292 */               for (int b = 0; b < 385; b++) {
/* 352:293 */                 this.si3040.disk[unit][removable].write(0);
/* 353:    */               }
/* 354:    */             }
/* 355:    */             catch (IOException e)
/* 356:    */             {
/* 357:296 */               System.out.println(e);
/* 358:    */             }
/* 359:298 */             System.out.println("New disk-file formatted!");
/* 360:    */           }
/* 361:    */           else
/* 362:    */           {
/* 363:300 */             this.si3040.sel[unit][removable] = 0;
/* 364:301 */             closeDisk(unit, removable, false);
/* 365:302 */             System.out.println("Set Format before mounting empty disk-file!");
/* 366:    */           }
/* 367:    */         }
/* 368:    */         else
/* 369:    */         {
/* 370:305 */           boolean test = true;
/* 371:    */           try
/* 372:    */           {
/* 373:307 */             for (int i = 0; i < 28; i++) {
/* 374:308 */               if (this.si3040.disk[unit][removable].read() != 0) {
/* 375:308 */                 test = false;
/* 376:    */               }
/* 377:    */             }
/* 378:310 */             if (this.si3040.disk[unit][removable].read() != 8) {
/* 379:310 */               test = false;
/* 380:    */             }
/* 381:311 */             if (!test)
/* 382:    */             {
/* 383:313 */               System.out.println("Wrong filetype: not m43 disk file");
/* 384:314 */               closeDisk(unit, removable, false);
/* 385:    */             }
/* 386:    */           }
/* 387:    */           catch (IOException e)
/* 388:    */           {
/* 389:317 */             System.out.println(e);
/* 390:    */           }
/* 391:    */         }
/* 392:    */       }
/* 393:    */     }
/* 394:    */     else
/* 395:    */     {
/* 396:321 */       System.out.println("No file selected");
/* 397:    */     }
/* 398:    */   }
/* 399:    */   
/* 400:    */   public void closeDisk(int unit, int removable, boolean keepname)
/* 401:    */   {
/* 402:326 */     if (this.si3040.sel[unit][removable] != 0)
/* 403:    */     {
/* 404:    */       try
/* 405:    */       {
/* 406:328 */         this.si3040.disk[unit][removable].close();
/* 407:    */       }
/* 408:    */       catch (IOException e) {}
/* 409:332 */       this.si3040.disk[unit][removable] = null;
/* 410:333 */       this.si3040.sel[unit][removable] = 0;
/* 411:334 */       this.si3040.track[unit] = 0;
/* 412:    */     }
/* 413:336 */     if (!keepname)
/* 414:    */     {
/* 415:337 */       String rtex = " fixed";
/* 416:337 */       if (removable > 0) {
/* 417:337 */         rtex = " rem";
/* 418:    */       }
/* 419:338 */       this.junit[unit][removable].setText("Unit " + unit + rtex);
/* 420:339 */       this.si3040.data.setProp("Disk4043-" + unit + "-" + removable);
/* 421:    */     }
/* 422:    */   }
/* 423:    */   
/* 424:    */   private static class ImageFileFilter
/* 425:    */     extends FileFilter
/* 426:    */   {
/* 427:    */     public boolean accept(File file)
/* 428:    */     {
/* 429:346 */       if (file == null) {
/* 430:347 */         return false;
/* 431:    */       }
/* 432:348 */       return (file.isDirectory()) || (file.getName().toLowerCase().endsWith(".m43"));
/* 433:    */     }
/* 434:    */     
/* 435:    */     public String getDescription()
/* 436:    */     {
/* 437:352 */       return "m43 disk files (*.m43)";
/* 438:    */     }
/* 439:    */   }
/* 440:    */   
/* 441:365 */   JMenu[][] junit = { { null, null }, { null, null }, { null, null }, { null, null } };
/* 442:    */   JFileChooser chooser;
/* 443:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.Disk4043
 * JD-Core Version:    0.7.0.1
 */