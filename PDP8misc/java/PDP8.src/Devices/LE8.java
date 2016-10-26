/*    1:     */ package Devices;
/*    2:     */ 
/*    3:     */ import Logic.BusRegMem;
/*    4:     */ import Logic.Constants;
/*    5:     */ import Logic.LPT;
/*    6:     */ import java.awt.Color;
/*    7:     */ import java.awt.Container;
/*    8:     */ import java.awt.Cursor;
/*    9:     */ import java.awt.Dimension;
/*   10:     */ import java.awt.Font;
/*   11:     */ import java.awt.FontMetrics;
/*   12:     */ import java.awt.Graphics;
/*   13:     */ import java.awt.Graphics2D;
/*   14:     */ import java.awt.GridBagConstraints;
/*   15:     */ import java.awt.GridBagLayout;
/*   16:     */ import java.awt.Insets;
/*   17:     */ import java.awt.Rectangle;
/*   18:     */ import java.awt.event.ActionEvent;
/*   19:     */ import java.awt.event.ActionListener;
/*   20:     */ import java.awt.event.WindowAdapter;
/*   21:     */ import java.awt.event.WindowEvent;
/*   22:     */ import java.awt.print.PageFormat;
/*   23:     */ import java.awt.print.Paper;
/*   24:     */ import java.awt.print.Printable;
/*   25:     */ import java.awt.print.PrinterException;
/*   26:     */ import java.awt.print.PrinterJob;
/*   27:     */ import java.io.BufferedReader;
/*   28:     */ import java.io.BufferedWriter;
/*   29:     */ import java.io.File;
/*   30:     */ import java.io.FileReader;
/*   31:     */ import java.io.FileWriter;
/*   32:     */ import java.io.IOException;
/*   33:     */ import java.io.PrintStream;
/*   34:     */ import java.util.ArrayList;
/*   35:     */ import java.util.List;
/*   36:     */ import javax.swing.AbstractAction;
/*   37:     */ import javax.swing.ActionMap;
/*   38:     */ import javax.swing.BorderFactory;
/*   39:     */ import javax.swing.GroupLayout;
/*   40:     */ import javax.swing.GroupLayout.Alignment;
/*   41:     */ import javax.swing.GroupLayout.ParallelGroup;
/*   42:     */ import javax.swing.InputMap;
/*   43:     */ import javax.swing.JCheckBoxMenuItem;
/*   44:     */ import javax.swing.JEditorPane;
/*   45:     */ import javax.swing.JFileChooser;
/*   46:     */ import javax.swing.JFrame;
/*   47:     */ import javax.swing.JLabel;
/*   48:     */ import javax.swing.JMenu;
/*   49:     */ import javax.swing.JMenuBar;
/*   50:     */ import javax.swing.JMenuItem;
/*   51:     */ import javax.swing.JPanel;
/*   52:     */ import javax.swing.JPopupMenu.Separator;
/*   53:     */ import javax.swing.JRadioButton;
/*   54:     */ import javax.swing.JScrollPane;
/*   55:     */ import javax.swing.JSeparator;
/*   56:     */ import javax.swing.KeyStroke;
/*   57:     */ import javax.swing.event.UndoableEditEvent;
/*   58:     */ import javax.swing.event.UndoableEditListener;
/*   59:     */ import javax.swing.text.Document;
/*   60:     */ import javax.swing.undo.CannotRedoException;
/*   61:     */ import javax.swing.undo.CannotUndoException;
/*   62:     */ import javax.swing.undo.UndoManager;
/*   63:     */ 
/*   64:     */ public class LE8
/*   65:     */   extends JFrame
/*   66:     */   implements Constants
/*   67:     */ {
/*   68:     */   public LPT lpt;
/*   69:     */   public StringBuilder docStringBuilder;
/*   70:  41 */   private File fileName = new File("");
/*   71:     */   private LE8Search search;
/*   72:     */   private Document editorPaneDocument;
/*   73:  44 */   protected UndoHandler undoHandler = new UndoHandler();
/*   74:  45 */   protected UndoManager undoManager = new UndoManager();
/*   75:  46 */   private UndoAction undoAction = null;
/*   76:  47 */   private RedoAction redoAction = null;
/*   77:  48 */   private boolean printHeaders = true;
/*   78:     */   private JRadioButton clearButton;
/*   79:     */   private JMenuItem clearMenu;
/*   80:     */   private JMenuItem copyMenu;
/*   81:     */   private JMenuItem cutMenu;
/*   82:     */   private JEditorPane editPane;
/*   83:     */   private JMenuItem exampleTextMenu;
/*   84:     */   private JMenuItem findMenu;
/*   85:     */   private JCheckBoxMenuItem headerMenu1;
/*   86:     */   private JLabel jLabel1;
/*   87:     */   private JMenu jMenu1;
/*   88:     */   private JMenu jMenu2;
/*   89:     */   private JMenu jMenu3;
/*   90:     */   private JMenuBar jMenuBar1;
/*   91:     */   private JPanel jPanel1;
/*   92:     */   private JPanel jPanel2;
/*   93:     */   private JPanel jPanel3;
/*   94:     */   private JPanel jPanel4;
/*   95:     */   private JRadioButton jRadioButton2;
/*   96:     */   private JRadioButton jRadioButton5;
/*   97:     */   private JScrollPane jScrollPane1;
/*   98:     */   private JSeparator jSeparator1;
/*   99:     */   private JPopupMenu.Separator jSeparator2;
/*  100:     */   public JRadioButton lineButton;
/*  101:     */   private JCheckBoxMenuItem lineMenu;
/*  102:     */   private JMenuItem loadMenu;
/*  103:     */   private JRadioButton onlineButton;
/*  104:     */   private JCheckBoxMenuItem onlineMenuItem;
/*  105:     */   private JMenuItem pasteMenu;
/*  106:     */   private JRadioButton powerButton;
/*  107:     */   private JCheckBoxMenuItem powerMenuItem;
/*  108:     */   private JMenuItem printMenu;
/*  109:     */   private JMenuItem redoMenu;
/*  110:     */   private JMenuItem saveAsMenu;
/*  111:     */   private JMenuItem saveMenu;
/*  112:     */   private JMenuItem selectAllMenu;
/*  113:     */   private JMenuItem undoMenu;
/*  114:     */   
/*  115:     */   public LE8(LPT lpt)
/*  116:     */   {
/*  117:  52 */     this.lpt = lpt;
/*  118:  53 */     setTitle("LPT");
/*  119:  54 */     this.docStringBuilder = new StringBuilder(1000);
/*  120:  55 */     initComponents();
/*  121:  56 */     this.search = new LE8Search();
/*  122:     */     
/*  123:  58 */     this.editorPaneDocument = this.editPane.getDocument();
/*  124:  59 */     this.editorPaneDocument.addUndoableEditListener(this.undoHandler);
/*  125:  60 */     this.editPane.setFont(new Font("Monospaced", 1, 12));
/*  126:     */     
/*  127:  62 */     KeyStroke undoKeystroke = KeyStroke.getKeyStroke(90, 128);
/*  128:  63 */     KeyStroke redoKeystroke = KeyStroke.getKeyStroke(89, 128);
/*  129:     */     
/*  130:  65 */     this.undoAction = new UndoAction();
/*  131:  66 */     this.editPane.getInputMap().put(undoKeystroke, "undoKeystroke");
/*  132:  67 */     this.editPane.getActionMap().put("undoKeystroke", this.undoAction);
/*  133:     */     
/*  134:  69 */     this.redoAction = new RedoAction();
/*  135:  70 */     this.editPane.getInputMap().put(redoKeystroke, "redoKeystroke");
/*  136:  71 */     this.editPane.getActionMap().put("redoKeystroke", this.redoAction);
/*  137:     */   }
/*  138:     */   
/*  139:     */   class UndoHandler
/*  140:     */     implements UndoableEditListener
/*  141:     */   {
/*  142:     */     UndoHandler() {}
/*  143:     */     
/*  144:     */     public void undoableEditHappened(UndoableEditEvent e)
/*  145:     */     {
/*  146:  78 */       LE8.this.undoManager.addEdit(e.getEdit());
/*  147:  79 */       LE8.this.undoAction.updateUndoState();
/*  148:  80 */       LE8.this.redoAction.updateRedoState();
/*  149:     */     }
/*  150:     */   }
/*  151:     */   
/*  152:     */   class UndoAction
/*  153:     */     extends AbstractAction
/*  154:     */   {
/*  155:     */     public UndoAction()
/*  156:     */     {
/*  157:  87 */       super();
/*  158:  88 */       setEnabled(false);
/*  159:     */     }
/*  160:     */     
/*  161:     */     public void actionPerformed(ActionEvent e)
/*  162:     */     {
/*  163:     */       try
/*  164:     */       {
/*  165:  93 */         LE8.this.undoManager.undo();
/*  166:     */       }
/*  167:     */       catch (CannotUndoException ex)
/*  168:     */       {
/*  169:  95 */         System.out.println("Unable to undo: " + ex);
/*  170:     */       }
/*  171:  97 */       updateUndoState();
/*  172:  98 */       LE8.this.redoAction.updateRedoState();
/*  173:     */     }
/*  174:     */     
/*  175:     */     protected void updateUndoState()
/*  176:     */     {
/*  177: 102 */       if (LE8.this.undoManager.canUndo())
/*  178:     */       {
/*  179: 103 */         setEnabled(true);
/*  180: 104 */         putValue("Name", LE8.this.undoManager.getUndoPresentationName());
/*  181:     */       }
/*  182:     */       else
/*  183:     */       {
/*  184: 106 */         setEnabled(false);
/*  185: 107 */         putValue("Name", "Undo");
/*  186:     */       }
/*  187:     */     }
/*  188:     */   }
/*  189:     */   
/*  190:     */   class RedoAction
/*  191:     */     extends AbstractAction
/*  192:     */   {
/*  193:     */     public RedoAction()
/*  194:     */     {
/*  195: 116 */       super();
/*  196: 117 */       setEnabled(false);
/*  197:     */     }
/*  198:     */     
/*  199:     */     public void actionPerformed(ActionEvent e)
/*  200:     */     {
/*  201:     */       try
/*  202:     */       {
/*  203: 122 */         LE8.this.undoManager.redo();
/*  204:     */       }
/*  205:     */       catch (CannotRedoException ex)
/*  206:     */       {
/*  207: 124 */         System.out.println("Unable to redo: " + ex);
/*  208:     */       }
/*  209: 126 */       updateRedoState();
/*  210: 127 */       LE8.this.undoAction.updateUndoState();
/*  211:     */     }
/*  212:     */     
/*  213:     */     protected void updateRedoState()
/*  214:     */     {
/*  215: 131 */       if (LE8.this.undoManager.canRedo())
/*  216:     */       {
/*  217: 132 */         setEnabled(true);
/*  218: 133 */         putValue("Name", LE8.this.undoManager.getRedoPresentationName());
/*  219:     */       }
/*  220:     */       else
/*  221:     */       {
/*  222: 135 */         setEnabled(false);
/*  223: 136 */         putValue("Name", "Redo");
/*  224:     */       }
/*  225:     */     }
/*  226:     */   }
/*  227:     */   
/*  228:     */   class RandomText
/*  229:     */   {
/*  230: 143 */     String text = null;
/*  231: 144 */     int pos = 0;
/*  232: 145 */     String eol = "\n";
/*  233: 146 */     String ff = "\f";
/*  234: 147 */     String tab = "\t";
/*  235: 148 */     String rub = "";
/*  236: 149 */     String empty = "";
/*  237:     */     
/*  238:     */     public RandomText()
/*  239:     */     {
/*  240: 152 */       this.text = LE8.this.editPane.getText();
/*  241: 153 */       this.pos = 0;
/*  242:     */     }
/*  243:     */     
/*  244:     */     public void setPointer(long to)
/*  245:     */     {
/*  246: 157 */       this.pos = ((int)to);
/*  247:     */     }
/*  248:     */     
/*  249:     */     public long getPointer()
/*  250:     */     {
/*  251: 161 */       return this.pos;
/*  252:     */     }
/*  253:     */     
/*  254:     */     public String readLine()
/*  255:     */     {
/*  256: 165 */       String result = null;
/*  257: 166 */       if (this.text.startsWith(this.ff, this.pos))
/*  258:     */       {
/*  259: 167 */         this.pos += 1;
/*  260: 168 */         return this.ff;
/*  261:     */       }
/*  262: 170 */       int index = this.text.indexOf(this.eol, this.pos);
/*  263: 171 */       if (index >= 0)
/*  264:     */       {
/*  265: 172 */         result = this.text.substring(this.pos, index);
/*  266: 173 */         this.pos = (index + this.eol.length());
/*  267: 174 */         result = result.replace(this.empty, "");
/*  268: 175 */         result = result.replace(this.rub, "");
/*  269:     */       }
/*  270: 177 */       if ((result != null) && (result.indexOf(this.tab) >= 0))
/*  271:     */       {
/*  272: 178 */         int postab = 0;
/*  273: 179 */         String restab = "";
/*  274: 180 */         for (int i = 0; i < result.length(); i++)
/*  275:     */         {
/*  276: 181 */           String strtab = result.substring(i, i + 1);
/*  277: 182 */           if (strtab.equals(this.tab))
/*  278:     */           {
/*  279:     */             do
/*  280:     */             {
/*  281: 184 */               restab = restab + " ";
/*  282: 185 */               postab++;
/*  283: 186 */             } while (postab % 8 != 0);
/*  284:     */           }
/*  285:     */           else
/*  286:     */           {
/*  287: 188 */             restab = restab + strtab;
/*  288: 189 */             postab++;
/*  289:     */           }
/*  290:     */         }
/*  291: 192 */         return restab;
/*  292:     */       }
/*  293: 194 */       return result;
/*  294:     */     }
/*  295:     */     
/*  296:     */     public void close()
/*  297:     */     {
/*  298: 198 */       this.text = null;
/*  299:     */     }
/*  300:     */   }
/*  301:     */   
/*  302:     */   public class FilePrintHelper
/*  303:     */   {
/*  304:     */     List<Entry> pageinfo;
/*  305:     */     
/*  306:     */     public FilePrintHelper()
/*  307:     */     {
/*  308: 207 */       this.pageinfo = new ArrayList();
/*  309:     */     }
/*  310:     */     
/*  311:     */     public void createPage(int page)
/*  312:     */     {
/*  313: 211 */       for (int i = this.pageinfo.size(); i <= page; i++) {
/*  314: 212 */         this.pageinfo.add(new Entry());
/*  315:     */       }
/*  316:     */     }
/*  317:     */     
/*  318:     */     public boolean knownPage(int page)
/*  319:     */     {
/*  320: 217 */       return page < this.pageinfo.size();
/*  321:     */     }
/*  322:     */     
/*  323:     */     public long getFileOffset(int page)
/*  324:     */     {
/*  325: 221 */       Entry entry = (Entry)this.pageinfo.get(page);
/*  326: 222 */       return entry.fileoffset;
/*  327:     */     }
/*  328:     */     
/*  329:     */     public void setFileOffset(int page, long fileoffset)
/*  330:     */     {
/*  331: 226 */       Entry entry = (Entry)this.pageinfo.get(page);
/*  332: 227 */       entry.fileoffset = fileoffset;
/*  333:     */     }
/*  334:     */     
/*  335:     */     class Entry
/*  336:     */     {
/*  337:     */       public long fileoffset;
/*  338:     */       
/*  339:     */       public Entry()
/*  340:     */       {
/*  341: 235 */         this.fileoffset = -1L;
/*  342:     */       }
/*  343:     */     }
/*  344:     */   }
/*  345:     */   
/*  346:     */   public class TextPrinter
/*  347:     */     implements Printable
/*  348:     */   {
/*  349:     */     private static final int RESMUL = 4;
/*  350:     */     private static final int FONTSIZE = 9;
/*  351:     */     private static final char FF = '\f';
/*  352:     */     private PrinterJob pjob;
/*  353:     */     private PageFormat pageformat;
/*  354:     */     private LE8.FilePrintHelper fph;
/*  355: 250 */     private String fname = "LP02-LA180-Output";
/*  356:     */     private LE8.RandomText in;
/*  357:     */     private Paper pa;
/*  358:     */     
/*  359:     */     public TextPrinter()
/*  360:     */     {
/*  361: 256 */       this.pjob = PrinterJob.getPrinterJob();
/*  362:     */     }
/*  363:     */     
/*  364:     */     public boolean setupPageFormat()
/*  365:     */     {
/*  366: 261 */       PageFormat defaultPF = this.pjob.defaultPage();
/*  367: 262 */       this.pjob.setJobName(this.fname);
/*  368: 263 */       this.pa = defaultPF.getPaper();
/*  369: 264 */       double mm10 = 28.346456692913389D;
/*  370: 265 */       this.pa.setImageableArea(mm10, mm10, this.pa.getWidth() - 2.0D * mm10, this.pa.getHeight() - 2.0D * mm10);
/*  371: 266 */       defaultPF.setPaper(this.pa);
/*  372: 267 */       this.pageformat = this.pjob.pageDialog(defaultPF);
/*  373: 268 */       this.pjob.setPrintable(this, this.pageformat);
/*  374: 269 */       return this.pageformat != defaultPF;
/*  375:     */     }
/*  376:     */     
/*  377:     */     public boolean setupJobOptions()
/*  378:     */     {
/*  379: 273 */       return this.pjob.printDialog();
/*  380:     */     }
/*  381:     */     
/*  382:     */     public void printFile()
/*  383:     */       throws PrinterException, IOException
/*  384:     */     {
/*  385: 278 */       this.fph = new LE8.FilePrintHelper(LE8.this);
/*  386: 279 */       this.in = new LE8.RandomText(LE8.this);
/*  387: 280 */       this.pjob.print();
/*  388: 281 */       this.in.close();
/*  389:     */     }
/*  390:     */     
/*  391:     */     public int print(Graphics g, PageFormat pf, int page)
/*  392:     */       throws PrinterException
/*  393:     */     {
/*  394: 287 */       int ret = 0;
/*  395: 288 */       String line = null;
/*  396: 289 */       if (this.fph.knownPage(page))
/*  397:     */       {
/*  398: 290 */         this.in.setPointer(this.fph.getFileOffset(page));
/*  399: 291 */         line = this.in.readLine();
/*  400:     */       }
/*  401:     */       else
/*  402:     */       {
/*  403: 293 */         long offset = this.in.getPointer();
/*  404: 294 */         line = this.in.readLine();
/*  405: 295 */         if (line == null)
/*  406:     */         {
/*  407: 296 */           ret = 1;
/*  408:     */         }
/*  409:     */         else
/*  410:     */         {
/*  411: 298 */           this.fph.createPage(page);
/*  412: 299 */           this.fph.setFileOffset(page, offset);
/*  413:     */         }
/*  414:     */       }
/*  415: 302 */       if (ret == 0)
/*  416:     */       {
/*  417: 303 */         Graphics2D g2 = (Graphics2D)g;
/*  418: 304 */         g2.scale(0.25D, 0.25D);
/*  419: 305 */         g2.setColor(Color.black);
/*  420: 306 */         g2.setFont(new Font("Monospaced", 1, 36));
/*  421: 307 */         int yd = g2.getFontMetrics().getHeight();
/*  422: 308 */         g2.setFont(new Font("Monospaced", 1, 36));
/*  423: 309 */         int ypos = (int)pf.getImageableY() * 4;
/*  424: 310 */         int xpos = (int)pf.getImageableX() * 4;
/*  425: 311 */         int ymax = ypos + (int)pf.getImageableHeight() * 4 - yd;
/*  426: 312 */         ypos += yd;
/*  427: 313 */         if (LE8.this.printHeaders)
/*  428:     */         {
/*  429: 314 */           g.drawString(this.fname + ", Page " + (page + 1), xpos, ypos);
/*  430: 315 */           g.drawLine(xpos, ypos + 24, xpos + (int)pf.getImageableWidth() * 4, ypos + 24);
/*  431:     */           
/*  432:     */ 
/*  433:     */ 
/*  434:     */ 
/*  435: 320 */           ypos += 2 * yd;
/*  436:     */         }
/*  437: 322 */         g2.setFont(new Font("Monospaced", 1, 36));
/*  438: 323 */         while (line != null) {
/*  439: 324 */           if (line.charAt(0) == '\f')
/*  440:     */           {
/*  441: 325 */             if ((page > 0) || (ypos > yd * 10)) {
/*  442:     */               break;
/*  443:     */             }
/*  444: 328 */             line = this.in.readLine();
/*  445:     */           }
/*  446:     */           else
/*  447:     */           {
/*  448: 331 */             g.drawString(line, xpos, ypos);
/*  449: 332 */             ypos += yd;
/*  450: 333 */             if (ypos >= ymax) {
/*  451:     */               break;
/*  452:     */             }
/*  453: 336 */             line = this.in.readLine();
/*  454:     */           }
/*  455:     */         }
/*  456:     */       }
/*  457: 339 */       return ret;
/*  458:     */     }
/*  459:     */   }
/*  460:     */   
/*  461:     */   private void initComponents()
/*  462:     */   {
/*  463: 348 */     this.jScrollPane1 = new JScrollPane();
/*  464: 349 */     this.editPane = new JEditorPane();
/*  465: 350 */     this.jPanel1 = new JPanel();
/*  466: 351 */     this.jPanel2 = new JPanel();
/*  467: 352 */     this.jPanel3 = new JPanel();
/*  468: 353 */     this.powerButton = new JRadioButton();
/*  469: 354 */     this.jRadioButton2 = new JRadioButton();
/*  470: 355 */     this.lineButton = new JRadioButton();
/*  471: 356 */     this.onlineButton = new JRadioButton();
/*  472: 357 */     this.jRadioButton5 = new JRadioButton();
/*  473: 358 */     this.clearButton = new JRadioButton();
/*  474: 359 */     this.jPanel4 = new JPanel();
/*  475: 360 */     this.jLabel1 = new JLabel();
/*  476: 361 */     this.jMenuBar1 = new JMenuBar();
/*  477: 362 */     this.jMenu1 = new JMenu();
/*  478: 363 */     this.loadMenu = new JMenuItem();
/*  479: 364 */     this.saveMenu = new JMenuItem();
/*  480: 365 */     this.saveAsMenu = new JMenuItem();
/*  481: 366 */     this.jMenu2 = new JMenu();
/*  482: 367 */     this.selectAllMenu = new JMenuItem();
/*  483: 368 */     this.copyMenu = new JMenuItem();
/*  484: 369 */     this.cutMenu = new JMenuItem();
/*  485: 370 */     this.pasteMenu = new JMenuItem();
/*  486: 371 */     this.undoMenu = new JMenuItem();
/*  487: 372 */     this.redoMenu = new JMenuItem();
/*  488: 373 */     this.jSeparator1 = new JSeparator();
/*  489: 374 */     this.findMenu = new JMenuItem();
/*  490: 375 */     this.jMenu3 = new JMenu();
/*  491: 376 */     this.powerMenuItem = new JCheckBoxMenuItem();
/*  492: 377 */     this.onlineMenuItem = new JCheckBoxMenuItem();
/*  493: 378 */     this.lineMenu = new JCheckBoxMenuItem();
/*  494: 379 */     this.jSeparator2 = new JPopupMenu.Separator();
/*  495: 380 */     this.clearMenu = new JMenuItem();
/*  496: 381 */     this.exampleTextMenu = new JMenuItem();
/*  497: 382 */     this.headerMenu1 = new JCheckBoxMenuItem();
/*  498: 383 */     this.printMenu = new JMenuItem();
/*  499:     */     
/*  500: 385 */     setTitle("LP02/LA180");
/*  501: 386 */     setBackground(new Color(153, 153, 153));
/*  502: 387 */     setBounds(new Rectangle(0, 0, 0, 0));
/*  503: 388 */     setCursor(new Cursor(0));
/*  504: 389 */     setForeground(new Color(102, 102, 102));
/*  505: 390 */     setMinimumSize(new Dimension(900, 650));
/*  506: 391 */     setName("LE8");
/*  507: 392 */     addWindowListener(new WindowAdapter()
/*  508:     */     {
/*  509:     */       public void windowClosing(WindowEvent evt)
/*  510:     */       {
/*  511: 394 */         LE8.this.formWindowClosing(evt);
/*  512:     */       }
/*  513: 396 */     });
/*  514: 397 */     getContentPane().setLayout(new GridBagLayout());
/*  515:     */     
/*  516: 399 */     this.jScrollPane1.setHorizontalScrollBar(null);
/*  517: 400 */     this.jScrollPane1.setMinimumSize(new Dimension(302, 202));
/*  518:     */     
/*  519: 402 */     this.editPane.setBorder(BorderFactory.createBevelBorder(1));
/*  520: 403 */     this.editPane.setFont(new Font("Monospaced", 0, 14));
/*  521: 404 */     this.editPane.setCursor(new Cursor(2));
/*  522: 405 */     this.editPane.setMargin(new Insets(3, 10, 3, 10));
/*  523: 406 */     this.editPane.setMinimumSize(new Dimension(350, 20));
/*  524: 407 */     this.editPane.setPreferredSize(new Dimension(350, 200));
/*  525: 408 */     this.jScrollPane1.setViewportView(this.editPane);
/*  526:     */     
/*  527: 410 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*  528: 411 */     gridBagConstraints.gridx = 1;
/*  529: 412 */     gridBagConstraints.gridy = 3;
/*  530: 413 */     gridBagConstraints.fill = 1;
/*  531: 414 */     gridBagConstraints.ipadx = 350;
/*  532: 415 */     gridBagConstraints.ipady = 200;
/*  533: 416 */     gridBagConstraints.anchor = 18;
/*  534: 417 */     gridBagConstraints.weightx = 1.0D;
/*  535: 418 */     gridBagConstraints.weighty = 1.0D;
/*  536: 419 */     getContentPane().add(this.jScrollPane1, gridBagConstraints);
/*  537:     */     
/*  538: 421 */     this.jPanel1.setBackground(new Color(102, 102, 102));
/*  539: 422 */     this.jPanel1.setMinimumSize(new Dimension(75, 10));
/*  540: 423 */     this.jPanel1.setPreferredSize(new Dimension(75, 10));
/*  541:     */     
/*  542: 425 */     GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
/*  543: 426 */     this.jPanel1.setLayout(jPanel1Layout);
/*  544: 427 */     jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 165, 32767));
/*  545:     */     
/*  546:     */ 
/*  547:     */ 
/*  548: 431 */     jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 30, 32767));
/*  549:     */     
/*  550:     */ 
/*  551:     */ 
/*  552:     */ 
/*  553: 436 */     gridBagConstraints = new GridBagConstraints();
/*  554: 437 */     gridBagConstraints.gridx = 0;
/*  555: 438 */     gridBagConstraints.gridy = 3;
/*  556: 439 */     gridBagConstraints.fill = 3;
/*  557: 440 */     gridBagConstraints.ipadx = 90;
/*  558: 441 */     gridBagConstraints.ipady = 10;
/*  559: 442 */     gridBagConstraints.anchor = 18;
/*  560: 443 */     gridBagConstraints.insets = new Insets(525, 0, 0, 0);
/*  561: 444 */     getContentPane().add(this.jPanel1, gridBagConstraints);
/*  562:     */     
/*  563: 446 */     this.jPanel2.setBackground(new Color(102, 102, 102));
/*  564: 447 */     this.jPanel2.setPreferredSize(new Dimension(500, 10));
/*  565:     */     
/*  566: 449 */     GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
/*  567: 450 */     this.jPanel2.setLayout(jPanel2Layout);
/*  568: 451 */     jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 1005, 32767));
/*  569:     */     
/*  570:     */ 
/*  571:     */ 
/*  572: 455 */     jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 20, 32767));
/*  573:     */     
/*  574:     */ 
/*  575:     */ 
/*  576:     */ 
/*  577: 460 */     gridBagConstraints = new GridBagConstraints();
/*  578: 461 */     gridBagConstraints.gridx = 0;
/*  579: 462 */     gridBagConstraints.gridy = 2;
/*  580: 463 */     gridBagConstraints.gridwidth = 3;
/*  581: 464 */     gridBagConstraints.fill = 2;
/*  582: 465 */     gridBagConstraints.ipadx = 300;
/*  583: 466 */     gridBagConstraints.ipady = 10;
/*  584: 467 */     gridBagConstraints.anchor = 18;
/*  585: 468 */     getContentPane().add(this.jPanel2, gridBagConstraints);
/*  586:     */     
/*  587: 470 */     this.jPanel3.setBackground(new Color(153, 153, 153));
/*  588: 471 */     this.jPanel3.setMinimumSize(new Dimension(75, 60));
/*  589: 472 */     this.jPanel3.setPreferredSize(new Dimension(90, 60));
/*  590: 473 */     this.jPanel3.setLayout(new GridBagLayout());
/*  591:     */     
/*  592: 475 */     this.powerButton.setBackground(new Color(153, 153, 153));
/*  593: 476 */     this.powerButton.setFont(new Font("Tahoma", 0, 10));
/*  594: 477 */     this.powerButton.setSelected(true);
/*  595: 478 */     this.powerButton.setText("Power");
/*  596: 479 */     this.powerButton.setHorizontalAlignment(11);
/*  597: 480 */     this.powerButton.setHorizontalTextPosition(10);
/*  598: 481 */     this.powerButton.setMaximumSize(new Dimension(65, 21));
/*  599: 482 */     this.powerButton.setMinimumSize(new Dimension(65, 21));
/*  600: 483 */     this.powerButton.setPreferredSize(new Dimension(65, 21));
/*  601: 484 */     this.powerButton.addActionListener(new ActionListener()
/*  602:     */     {
/*  603:     */       public void actionPerformed(ActionEvent evt)
/*  604:     */       {
/*  605: 486 */         LE8.this.powerButtonActionPerformed(evt);
/*  606:     */       }
/*  607: 488 */     });
/*  608: 489 */     gridBagConstraints = new GridBagConstraints();
/*  609: 490 */     gridBagConstraints.gridy = 0;
/*  610: 491 */     gridBagConstraints.fill = 2;
/*  611: 492 */     gridBagConstraints.anchor = 18;
/*  612: 493 */     gridBagConstraints.insets = new Insets(3, 3, 3, 3);
/*  613: 494 */     this.jPanel3.add(this.powerButton, gridBagConstraints);
/*  614:     */     
/*  615: 496 */     this.jRadioButton2.setBackground(new Color(153, 153, 153));
/*  616: 497 */     this.jRadioButton2.addActionListener(new ActionListener()
/*  617:     */     {
/*  618:     */       public void actionPerformed(ActionEvent evt)
/*  619:     */       {
/*  620: 499 */         LE8.this.jRadioButton2ActionPerformed(evt);
/*  621:     */       }
/*  622: 501 */     });
/*  623: 502 */     gridBagConstraints = new GridBagConstraints();
/*  624: 503 */     gridBagConstraints.gridx = 1;
/*  625: 504 */     gridBagConstraints.gridy = 0;
/*  626: 505 */     gridBagConstraints.anchor = 18;
/*  627: 506 */     gridBagConstraints.insets = new Insets(3, 3, 3, 3);
/*  628: 507 */     this.jPanel3.add(this.jRadioButton2, gridBagConstraints);
/*  629:     */     
/*  630: 509 */     this.lineButton.setBackground(new Color(153, 153, 153));
/*  631: 510 */     this.lineButton.setFont(new Font("Tahoma", 0, 10));
/*  632: 511 */     this.lineButton.setText("Lines");
/*  633: 512 */     this.lineButton.setMaximumSize(new Dimension(65, 21));
/*  634: 513 */     this.lineButton.setMinimumSize(new Dimension(65, 21));
/*  635: 514 */     this.lineButton.setPreferredSize(new Dimension(65, 21));
/*  636: 515 */     this.lineButton.addActionListener(new ActionListener()
/*  637:     */     {
/*  638:     */       public void actionPerformed(ActionEvent evt)
/*  639:     */       {
/*  640: 517 */         LE8.this.lineButtonActionPerformed(evt);
/*  641:     */       }
/*  642: 519 */     });
/*  643: 520 */     gridBagConstraints = new GridBagConstraints();
/*  644: 521 */     gridBagConstraints.gridx = 2;
/*  645: 522 */     gridBagConstraints.gridy = 0;
/*  646: 523 */     gridBagConstraints.fill = 2;
/*  647: 524 */     gridBagConstraints.anchor = 18;
/*  648: 525 */     gridBagConstraints.insets = new Insets(3, 3, 3, 3);
/*  649: 526 */     this.jPanel3.add(this.lineButton, gridBagConstraints);
/*  650:     */     
/*  651: 528 */     this.onlineButton.setBackground(new Color(153, 153, 153));
/*  652: 529 */     this.onlineButton.setFont(new Font("Tahoma", 0, 10));
/*  653: 530 */     this.onlineButton.setSelected(true);
/*  654: 531 */     this.onlineButton.setText("Online");
/*  655: 532 */     this.onlineButton.setHorizontalAlignment(11);
/*  656: 533 */     this.onlineButton.setHorizontalTextPosition(10);
/*  657: 534 */     this.onlineButton.setMaximumSize(new Dimension(65, 21));
/*  658: 535 */     this.onlineButton.setMinimumSize(new Dimension(65, 21));
/*  659: 536 */     this.onlineButton.setPreferredSize(new Dimension(65, 21));
/*  660: 537 */     this.onlineButton.addActionListener(new ActionListener()
/*  661:     */     {
/*  662:     */       public void actionPerformed(ActionEvent evt)
/*  663:     */       {
/*  664: 539 */         LE8.this.onlineButtonActionPerformed(evt);
/*  665:     */       }
/*  666: 541 */     });
/*  667: 542 */     gridBagConstraints = new GridBagConstraints();
/*  668: 543 */     gridBagConstraints.gridx = 0;
/*  669: 544 */     gridBagConstraints.gridy = 1;
/*  670: 545 */     gridBagConstraints.fill = 2;
/*  671: 546 */     gridBagConstraints.anchor = 18;
/*  672: 547 */     gridBagConstraints.insets = new Insets(3, 3, 3, 3);
/*  673: 548 */     this.jPanel3.add(this.onlineButton, gridBagConstraints);
/*  674:     */     
/*  675: 550 */     this.jRadioButton5.setBackground(new Color(153, 153, 153));
/*  676: 551 */     this.jRadioButton5.addActionListener(new ActionListener()
/*  677:     */     {
/*  678:     */       public void actionPerformed(ActionEvent evt)
/*  679:     */       {
/*  680: 553 */         LE8.this.jRadioButton5ActionPerformed(evt);
/*  681:     */       }
/*  682: 555 */     });
/*  683: 556 */     gridBagConstraints = new GridBagConstraints();
/*  684: 557 */     gridBagConstraints.gridx = 1;
/*  685: 558 */     gridBagConstraints.gridy = 1;
/*  686: 559 */     gridBagConstraints.anchor = 18;
/*  687: 560 */     gridBagConstraints.insets = new Insets(3, 3, 3, 3);
/*  688: 561 */     this.jPanel3.add(this.jRadioButton5, gridBagConstraints);
/*  689:     */     
/*  690: 563 */     this.clearButton.setBackground(new Color(153, 153, 153));
/*  691: 564 */     this.clearButton.setFont(new Font("Tahoma", 0, 10));
/*  692: 565 */     this.clearButton.setText("Clear");
/*  693: 566 */     this.clearButton.setMaximumSize(new Dimension(65, 21));
/*  694: 567 */     this.clearButton.setMinimumSize(new Dimension(65, 21));
/*  695: 568 */     this.clearButton.setPreferredSize(new Dimension(65, 21));
/*  696: 569 */     this.clearButton.addActionListener(new ActionListener()
/*  697:     */     {
/*  698:     */       public void actionPerformed(ActionEvent evt)
/*  699:     */       {
/*  700: 571 */         LE8.this.clearButtonActionPerformed(evt);
/*  701:     */       }
/*  702: 573 */     });
/*  703: 574 */     gridBagConstraints = new GridBagConstraints();
/*  704: 575 */     gridBagConstraints.gridx = 2;
/*  705: 576 */     gridBagConstraints.gridy = 1;
/*  706: 577 */     gridBagConstraints.fill = 2;
/*  707: 578 */     gridBagConstraints.anchor = 18;
/*  708: 579 */     gridBagConstraints.insets = new Insets(3, 3, 3, 3);
/*  709: 580 */     this.jPanel3.add(this.clearButton, gridBagConstraints);
/*  710:     */     
/*  711: 582 */     gridBagConstraints = new GridBagConstraints();
/*  712: 583 */     gridBagConstraints.gridx = 0;
/*  713: 584 */     gridBagConstraints.gridy = 0;
/*  714: 585 */     gridBagConstraints.gridheight = 2;
/*  715: 586 */     gridBagConstraints.fill = 1;
/*  716: 587 */     gridBagConstraints.anchor = 18;
/*  717: 588 */     getContentPane().add(this.jPanel3, gridBagConstraints);
/*  718:     */     
/*  719: 590 */     this.jPanel4.setBackground(new Color(102, 102, 102));
/*  720: 591 */     this.jPanel4.setPreferredSize(new Dimension(30, 10));
/*  721:     */     
/*  722: 593 */     GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
/*  723: 594 */     this.jPanel4.setLayout(jPanel4Layout);
/*  724: 595 */     jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 50, 32767));
/*  725:     */     
/*  726:     */ 
/*  727:     */ 
/*  728: 599 */     jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 30, 32767));
/*  729:     */     
/*  730:     */ 
/*  731:     */ 
/*  732:     */ 
/*  733: 604 */     gridBagConstraints = new GridBagConstraints();
/*  734: 605 */     gridBagConstraints.gridx = 2;
/*  735: 606 */     gridBagConstraints.gridy = 3;
/*  736: 607 */     gridBagConstraints.fill = 3;
/*  737: 608 */     gridBagConstraints.ipadx = 20;
/*  738: 609 */     gridBagConstraints.ipady = 10;
/*  739: 610 */     gridBagConstraints.anchor = 18;
/*  740: 611 */     gridBagConstraints.insets = new Insets(525, 0, 0, 0);
/*  741: 612 */     getContentPane().add(this.jPanel4, gridBagConstraints);
/*  742:     */     
/*  743: 614 */     this.jLabel1.setFont(new Font("Times New Roman", 1, 18));
/*  744: 615 */     this.jLabel1.setText("DEC LP02/LA180 Printer");
/*  745: 616 */     gridBagConstraints = new GridBagConstraints();
/*  746: 617 */     gridBagConstraints.gridx = 1;
/*  747: 618 */     gridBagConstraints.gridy = 0;
/*  748: 619 */     gridBagConstraints.fill = 2;
/*  749: 620 */     gridBagConstraints.ipady = 6;
/*  750: 621 */     gridBagConstraints.anchor = 18;
/*  751: 622 */     gridBagConstraints.insets = new Insets(22, 400, 0, 0);
/*  752: 623 */     getContentPane().add(this.jLabel1, gridBagConstraints);
/*  753:     */     
/*  754: 625 */     this.jMenu1.setText("File");
/*  755: 626 */     this.jMenu1.setPreferredSize(new Dimension(60, 19));
/*  756:     */     
/*  757: 628 */     this.loadMenu.setAccelerator(KeyStroke.getKeyStroke(79, 2));
/*  758: 629 */     this.loadMenu.setText("Open");
/*  759: 630 */     this.loadMenu.addActionListener(new ActionListener()
/*  760:     */     {
/*  761:     */       public void actionPerformed(ActionEvent evt)
/*  762:     */       {
/*  763: 632 */         LE8.this.loadMenuActionPerformed(evt);
/*  764:     */       }
/*  765: 634 */     });
/*  766: 635 */     this.jMenu1.add(this.loadMenu);
/*  767:     */     
/*  768: 637 */     this.saveMenu.setAccelerator(KeyStroke.getKeyStroke(83, 2));
/*  769: 638 */     this.saveMenu.setText("Save");
/*  770: 639 */     this.saveMenu.addActionListener(new ActionListener()
/*  771:     */     {
/*  772:     */       public void actionPerformed(ActionEvent evt)
/*  773:     */       {
/*  774: 641 */         LE8.this.saveMenuActionPerformed(evt);
/*  775:     */       }
/*  776: 643 */     });
/*  777: 644 */     this.jMenu1.add(this.saveMenu);
/*  778:     */     
/*  779: 646 */     this.saveAsMenu.setText("Save as...");
/*  780: 647 */     this.saveAsMenu.addActionListener(new ActionListener()
/*  781:     */     {
/*  782:     */       public void actionPerformed(ActionEvent evt)
/*  783:     */       {
/*  784: 649 */         LE8.this.saveAsMenuActionPerformed(evt);
/*  785:     */       }
/*  786: 651 */     });
/*  787: 652 */     this.jMenu1.add(this.saveAsMenu);
/*  788:     */     
/*  789: 654 */     this.jMenuBar1.add(this.jMenu1);
/*  790:     */     
/*  791: 656 */     this.jMenu2.setText("Edit");
/*  792: 657 */     this.jMenu2.setToolTipText("");
/*  793: 658 */     this.jMenu2.setPreferredSize(new Dimension(60, 19));
/*  794:     */     
/*  795: 660 */     this.selectAllMenu.setAccelerator(KeyStroke.getKeyStroke(65, 2));
/*  796: 661 */     this.selectAllMenu.setText("Select All");
/*  797: 662 */     this.selectAllMenu.addActionListener(new ActionListener()
/*  798:     */     {
/*  799:     */       public void actionPerformed(ActionEvent evt)
/*  800:     */       {
/*  801: 664 */         LE8.this.selectAllMenuActionPerformed(evt);
/*  802:     */       }
/*  803: 666 */     });
/*  804: 667 */     this.jMenu2.add(this.selectAllMenu);
/*  805:     */     
/*  806: 669 */     this.copyMenu.setAccelerator(KeyStroke.getKeyStroke(67, 2));
/*  807: 670 */     this.copyMenu.setText("Copy");
/*  808: 671 */     this.copyMenu.addActionListener(new ActionListener()
/*  809:     */     {
/*  810:     */       public void actionPerformed(ActionEvent evt)
/*  811:     */       {
/*  812: 673 */         LE8.this.copyMenuActionPerformed(evt);
/*  813:     */       }
/*  814: 675 */     });
/*  815: 676 */     this.jMenu2.add(this.copyMenu);
/*  816:     */     
/*  817: 678 */     this.cutMenu.setAccelerator(KeyStroke.getKeyStroke(88, 2));
/*  818: 679 */     this.cutMenu.setText("Cut");
/*  819: 680 */     this.cutMenu.addActionListener(new ActionListener()
/*  820:     */     {
/*  821:     */       public void actionPerformed(ActionEvent evt)
/*  822:     */       {
/*  823: 682 */         LE8.this.cutMenuActionPerformed(evt);
/*  824:     */       }
/*  825: 684 */     });
/*  826: 685 */     this.jMenu2.add(this.cutMenu);
/*  827:     */     
/*  828: 687 */     this.pasteMenu.setAccelerator(KeyStroke.getKeyStroke(86, 2));
/*  829: 688 */     this.pasteMenu.setText("Paste");
/*  830: 689 */     this.pasteMenu.addActionListener(new ActionListener()
/*  831:     */     {
/*  832:     */       public void actionPerformed(ActionEvent evt)
/*  833:     */       {
/*  834: 691 */         LE8.this.pasteMenuActionPerformed(evt);
/*  835:     */       }
/*  836: 693 */     });
/*  837: 694 */     this.jMenu2.add(this.pasteMenu);
/*  838:     */     
/*  839: 696 */     this.undoMenu.setAccelerator(KeyStroke.getKeyStroke(90, 2));
/*  840: 697 */     this.undoMenu.setText("Undo");
/*  841: 698 */     this.undoMenu.addActionListener(new ActionListener()
/*  842:     */     {
/*  843:     */       public void actionPerformed(ActionEvent evt)
/*  844:     */       {
/*  845: 700 */         LE8.this.undoMenuActionPerformed(evt);
/*  846:     */       }
/*  847: 702 */     });
/*  848: 703 */     this.jMenu2.add(this.undoMenu);
/*  849:     */     
/*  850: 705 */     this.redoMenu.setAccelerator(KeyStroke.getKeyStroke(89, 2));
/*  851: 706 */     this.redoMenu.setText("Redo");
/*  852: 707 */     this.redoMenu.addActionListener(new ActionListener()
/*  853:     */     {
/*  854:     */       public void actionPerformed(ActionEvent evt)
/*  855:     */       {
/*  856: 709 */         LE8.this.redoMenuActionPerformed(evt);
/*  857:     */       }
/*  858: 711 */     });
/*  859: 712 */     this.jMenu2.add(this.redoMenu);
/*  860: 713 */     this.jMenu2.add(this.jSeparator1);
/*  861:     */     
/*  862: 715 */     this.findMenu.setAccelerator(KeyStroke.getKeyStroke(70, 2));
/*  863: 716 */     this.findMenu.setText("Find");
/*  864: 717 */     this.findMenu.addActionListener(new ActionListener()
/*  865:     */     {
/*  866:     */       public void actionPerformed(ActionEvent evt)
/*  867:     */       {
/*  868: 719 */         LE8.this.findMenuActionPerformed(evt);
/*  869:     */       }
/*  870: 721 */     });
/*  871: 722 */     this.jMenu2.add(this.findMenu);
/*  872:     */     
/*  873: 724 */     this.jMenuBar1.add(this.jMenu2);
/*  874:     */     
/*  875: 726 */     this.jMenu3.setText("Printer");
/*  876: 727 */     this.jMenu3.setPreferredSize(new Dimension(60, 19));
/*  877:     */     
/*  878: 729 */     this.powerMenuItem.setSelected(true);
/*  879: 730 */     this.powerMenuItem.setText("Power");
/*  880: 731 */     this.powerMenuItem.addActionListener(new ActionListener()
/*  881:     */     {
/*  882:     */       public void actionPerformed(ActionEvent evt)
/*  883:     */       {
/*  884: 733 */         LE8.this.powerMenuItemActionPerformed(evt);
/*  885:     */       }
/*  886: 735 */     });
/*  887: 736 */     this.jMenu3.add(this.powerMenuItem);
/*  888:     */     
/*  889: 738 */     this.onlineMenuItem.setSelected(true);
/*  890: 739 */     this.onlineMenuItem.setText("Online");
/*  891: 740 */     this.onlineMenuItem.addActionListener(new ActionListener()
/*  892:     */     {
/*  893:     */       public void actionPerformed(ActionEvent evt)
/*  894:     */       {
/*  895: 742 */         LE8.this.onlineMenuItemActionPerformed(evt);
/*  896:     */       }
/*  897: 744 */     });
/*  898: 745 */     this.jMenu3.add(this.onlineMenuItem);
/*  899:     */     
/*  900: 747 */     this.lineMenu.setText("Lines");
/*  901: 748 */     this.lineMenu.addActionListener(new ActionListener()
/*  902:     */     {
/*  903:     */       public void actionPerformed(ActionEvent evt)
/*  904:     */       {
/*  905: 750 */         LE8.this.lineMenuActionPerformed(evt);
/*  906:     */       }
/*  907: 752 */     });
/*  908: 753 */     this.jMenu3.add(this.lineMenu);
/*  909: 754 */     this.jMenu3.add(this.jSeparator2);
/*  910:     */     
/*  911: 756 */     this.clearMenu.setText("Clear");
/*  912: 757 */     this.clearMenu.addActionListener(new ActionListener()
/*  913:     */     {
/*  914:     */       public void actionPerformed(ActionEvent evt)
/*  915:     */       {
/*  916: 759 */         LE8.this.clearMenuActionPerformed(evt);
/*  917:     */       }
/*  918: 761 */     });
/*  919: 762 */     this.jMenu3.add(this.clearMenu);
/*  920:     */     
/*  921: 764 */     this.exampleTextMenu.setText("Example Text");
/*  922: 765 */     this.exampleTextMenu.addActionListener(new ActionListener()
/*  923:     */     {
/*  924:     */       public void actionPerformed(ActionEvent evt)
/*  925:     */       {
/*  926: 767 */         LE8.this.exampleTextMenuActionPerformed(evt);
/*  927:     */       }
/*  928: 769 */     });
/*  929: 770 */     this.jMenu3.add(this.exampleTextMenu);
/*  930:     */     
/*  931: 772 */     this.headerMenu1.setSelected(true);
/*  932: 773 */     this.headerMenu1.setText("Headers");
/*  933: 774 */     this.headerMenu1.addActionListener(new ActionListener()
/*  934:     */     {
/*  935:     */       public void actionPerformed(ActionEvent evt)
/*  936:     */       {
/*  937: 776 */         LE8.this.headerMenu1ActionPerformed(evt);
/*  938:     */       }
/*  939: 778 */     });
/*  940: 779 */     this.jMenu3.add(this.headerMenu1);
/*  941:     */     
/*  942: 781 */     this.printMenu.setText("Print²");
/*  943: 782 */     this.printMenu.addActionListener(new ActionListener()
/*  944:     */     {
/*  945:     */       public void actionPerformed(ActionEvent evt)
/*  946:     */       {
/*  947: 784 */         LE8.this.printMenuActionPerformed(evt);
/*  948:     */       }
/*  949: 786 */     });
/*  950: 787 */     this.jMenu3.add(this.printMenu);
/*  951:     */     
/*  952: 789 */     this.jMenuBar1.add(this.jMenu3);
/*  953:     */     
/*  954: 791 */     setJMenuBar(this.jMenuBar1);
/*  955:     */     
/*  956: 793 */     pack();
/*  957:     */   }
/*  958:     */   
/*  959:     */   private void copyMenuActionPerformed(ActionEvent evt)
/*  960:     */   {
/*  961: 797 */     this.editPane.copy();
/*  962:     */   }
/*  963:     */   
/*  964:     */   private void cutMenuActionPerformed(ActionEvent evt)
/*  965:     */   {
/*  966: 801 */     this.editPane.cut();
/*  967:     */   }
/*  968:     */   
/*  969:     */   private void pasteMenuActionPerformed(ActionEvent evt)
/*  970:     */   {
/*  971: 805 */     this.editPane.paste();
/*  972:     */   }
/*  973:     */   
/*  974:     */   private void selectAllMenuActionPerformed(ActionEvent evt)
/*  975:     */   {
/*  976: 809 */     this.editPane.selectAll();
/*  977:     */   }
/*  978:     */   
/*  979:     */   private void exampleTextMenuActionPerformed(ActionEvent evt)
/*  980:     */   {
/*  981: 813 */     this.editPane.setText(LOREMIPSUM);
/*  982: 814 */     this.clearButton.setSelected(false);
/*  983:     */   }
/*  984:     */   
/*  985:     */   private void saveMenuActionPerformed(ActionEvent evt)
/*  986:     */   {
/*  987:     */     try
/*  988:     */     {
/*  989: 820 */       BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName));
/*  990: 821 */       writer.write(this.editPane.getText());
/*  991: 822 */       writer.close();
/*  992:     */     }
/*  993:     */     catch (IOException ioe)
/*  994:     */     {
/*  995: 824 */       this.editPane.setText("Sorry. Can't write file.");
/*  996:     */     }
/*  997:     */   }
/*  998:     */   
/*  999:     */   private void loadMenuActionPerformed(ActionEvent evt)
/* 1000:     */   {
/* 1001: 829 */     JFileChooser fileChooser = new JFileChooser();
/* 1002: 830 */     String dfile = this.lpt.data.getProp("LE8-Openfile");
/* 1003: 831 */     if (dfile != null) {
/* 1004: 832 */       fileChooser.setSelectedFile(new File(dfile));
/* 1005:     */     }
/* 1006: 834 */     if (fileChooser.showOpenDialog(this) == 0)
/* 1007:     */     {
/* 1008: 837 */       StringBuilder stringBuilder = new StringBuilder();
/* 1009:     */       try
/* 1010:     */       {
/* 1011: 839 */         this.fileName = fileChooser.getSelectedFile();
/* 1012: 840 */         BufferedReader reader = new BufferedReader(new FileReader(this.fileName));
/* 1013: 841 */         while (reader.ready()) {
/* 1014: 842 */           stringBuilder.append(reader.readLine()).append(eol);
/* 1015:     */         }
/* 1016: 844 */         reader.close();
/* 1017: 845 */         this.editPane.setText(stringBuilder.toString());
/* 1018: 846 */         this.lpt.data.setProp("LE8-Openfile", this.fileName.getCanonicalPath());
/* 1019: 847 */         this.clearButton.setSelected(false);
/* 1020:     */       }
/* 1021:     */       catch (IOException ioe)
/* 1022:     */       {
/* 1023: 849 */         this.editPane.setText("Sorry. Can't open file.");
/* 1024:     */       }
/* 1025:     */     }
/* 1026:     */   }
/* 1027:     */   
/* 1028:     */   private void saveAsMenuActionPerformed(ActionEvent evt)
/* 1029:     */   {
/* 1030: 855 */     JFileChooser fileChooser = new JFileChooser();
/* 1031: 856 */     String dfile = this.lpt.data.getProp("LE8-Openfile");
/* 1032: 857 */     if (dfile != null) {
/* 1033: 858 */       fileChooser.setSelectedFile(new File(dfile));
/* 1034:     */     }
/* 1035: 860 */     if (fileChooser.showSaveDialog(this) == 0) {
/* 1036:     */       try
/* 1037:     */       {
/* 1038: 863 */         this.fileName = fileChooser.getSelectedFile();
/* 1039: 864 */         BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName));
/* 1040: 865 */         writer.write(this.editPane.getText());
/* 1041: 866 */         writer.close();
/* 1042: 867 */         this.lpt.data.setProp("LE8-Openfile", this.fileName.getCanonicalPath());
/* 1043:     */       }
/* 1044:     */       catch (IOException ioe)
/* 1045:     */       {
/* 1046: 869 */         this.editPane.setText("Sorry. Can't write file.");
/* 1047:     */       }
/* 1048:     */     }
/* 1049:     */   }
/* 1050:     */   
/* 1051:     */   private void clearMenuActionPerformed(ActionEvent evt)
/* 1052:     */   {
/* 1053: 875 */     clearEditor();
/* 1054:     */   }
/* 1055:     */   
/* 1056:     */   private void clearButtonActionPerformed(ActionEvent evt)
/* 1057:     */   {
/* 1058: 879 */     clearEditor();
/* 1059:     */   }
/* 1060:     */   
/* 1061:     */   private void onlineButtonActionPerformed(ActionEvent evt)
/* 1062:     */   {
/* 1063: 883 */     setOnline(this.onlineButton.isSelected());
/* 1064:     */   }
/* 1065:     */   
/* 1066:     */   private void findMenuActionPerformed(ActionEvent evt)
/* 1067:     */   {
/* 1068: 901 */     this.search.setTextComponent(this.editPane);
/* 1069: 902 */     this.search.setVisible(true);
/* 1070: 903 */     this.search.setLocation(getLocation());
/* 1071:     */   }
/* 1072:     */   
/* 1073:     */   private void powerButtonActionPerformed(ActionEvent evt)
/* 1074:     */   {
/* 1075: 907 */     setPower(this.powerButton.isSelected());
/* 1076:     */   }
/* 1077:     */   
/* 1078:     */   private void jRadioButton2ActionPerformed(ActionEvent evt)
/* 1079:     */   {
/* 1080: 916 */     this.jRadioButton2.setSelected(false);
/* 1081:     */   }
/* 1082:     */   
/* 1083:     */   private void lineButtonActionPerformed(ActionEvent evt)
/* 1084:     */   {
/* 1085: 920 */     this.lpt.lptlinewise = this.lineButton.isSelected();
/* 1086: 921 */     this.lineMenu.setSelected(this.lineButton.isSelected());
/* 1087: 922 */     this.lpt.lptfast = false;
/* 1088:     */   }
/* 1089:     */   
/* 1090:     */   private void jRadioButton5ActionPerformed(ActionEvent evt)
/* 1091:     */   {
/* 1092: 926 */     this.jRadioButton5.setSelected(false);
/* 1093:     */   }
/* 1094:     */   
/* 1095:     */   private void undoMenuActionPerformed(ActionEvent evt)
/* 1096:     */   {
/* 1097: 930 */     this.undoAction.actionPerformed(evt);
/* 1098:     */   }
/* 1099:     */   
/* 1100:     */   private void redoMenuActionPerformed(ActionEvent evt)
/* 1101:     */   {
/* 1102: 934 */     this.redoAction.actionPerformed(evt);
/* 1103:     */   }
/* 1104:     */   
/* 1105:     */   private void printMenuActionPerformed(ActionEvent evt)
/* 1106:     */   {
/* 1107: 939 */     TextPrinter sfp = new TextPrinter();
/* 1108: 940 */     if ((sfp.setupPageFormat()) && 
/* 1109: 941 */       (sfp.setupJobOptions())) {
/* 1110:     */       try
/* 1111:     */       {
/* 1112: 943 */         sfp.printFile();
/* 1113:     */       }
/* 1114:     */       catch (Exception e)
/* 1115:     */       {
/* 1116: 945 */         System.err.println(e.toString());
/* 1117:     */       }
/* 1118:     */     }
/* 1119:     */   }
/* 1120:     */   
/* 1121:     */   private void formWindowClosing(WindowEvent evt)
/* 1122:     */   {
/* 1123: 952 */     this.lpt.data.CloseAllDevs();
/* 1124:     */   }
/* 1125:     */   
/* 1126:     */   private void lineMenuActionPerformed(ActionEvent evt)
/* 1127:     */   {
/* 1128: 956 */     if (this.lineButton.isSelected())
/* 1129:     */     {
/* 1130: 957 */       this.lineButton.setSelected(false);
/* 1131: 958 */       this.lineMenu.setSelected(false);
/* 1132:     */     }
/* 1133:     */     else
/* 1134:     */     {
/* 1135: 960 */       this.lineButton.setSelected(true);
/* 1136: 961 */       this.lineMenu.setSelected(true);
/* 1137:     */     }
/* 1138:     */   }
/* 1139:     */   
/* 1140:     */   private void onlineMenuItemActionPerformed(ActionEvent evt)
/* 1141:     */   {
/* 1142: 966 */     setOnline(!this.onlineButton.isSelected());
/* 1143:     */   }
/* 1144:     */   
/* 1145:     */   private void headerMenu1ActionPerformed(ActionEvent evt)
/* 1146:     */   {
/* 1147: 978 */     this.printHeaders = this.headerMenu1.isSelected();
/* 1148:     */   }
/* 1149:     */   
/* 1150:     */   private void powerMenuItemActionPerformed(ActionEvent evt)
/* 1151:     */   {
/* 1152: 982 */     setPower(!this.powerButton.isSelected());
/* 1153:     */   }
/* 1154:     */   
/* 1155:     */   private void setPower(boolean on)
/* 1156:     */   {
/* 1157: 996 */     if (on)
/* 1158:     */     {
/* 1159: 997 */       this.powerButton.setSelected(true);
/* 1160: 998 */       this.powerMenuItem.setSelected(true);
/* 1161:     */     }
/* 1162:     */     else
/* 1163:     */     {
/* 1164:1000 */       this.powerButton.setSelected(false);
/* 1165:1001 */       this.powerMenuItem.setSelected(false);
/* 1166:1002 */       setOnline(false);
/* 1167:     */     }
/* 1168:     */   }
/* 1169:     */   
/* 1170:     */   private void setOnline(boolean on)
/* 1171:     */   {
/* 1172:1007 */     if ((on & this.powerButton.isSelected()))
/* 1173:     */     {
/* 1174:1008 */       this.onlineButton.setSelected(true);
/* 1175:1009 */       this.onlineMenuItem.setSelected(true);
/* 1176:1010 */       this.lpt.lptonline = true;
/* 1177:1011 */       this.lpt.errflag = false;
/* 1178:     */       
/* 1179:1013 */       this.lpt.clearIntReq();
/* 1180:     */     }
/* 1181:     */     else
/* 1182:     */     {
/* 1183:1015 */       this.onlineButton.setSelected(false);
/* 1184:1016 */       this.onlineMenuItem.setSelected(false);
/* 1185:1017 */       this.lpt.lptonline = false;
/* 1186:1018 */       this.lpt.errflag = true;
/* 1187:1019 */       this.lpt.lptflag = true;
/* 1188:1020 */       this.lpt.setIntReq();
/* 1189:     */     }
/* 1190:     */   }
/* 1191:     */   
/* 1192:     */   public void clearEditor()
/* 1193:     */   {
/* 1194:1026 */     this.editPane.setText("");
/* 1195:1027 */     this.docStringBuilder.delete(0, this.docStringBuilder.length());
/* 1196:1028 */     this.clearButton.setSelected(true);
/* 1197:1029 */     this.lineButton.setSelected(false);
/* 1198:     */   }
/* 1199:     */   
/* 1200:     */   public void addEditor(StringBuilder arg)
/* 1201:     */   {
/* 1202:1033 */     this.docStringBuilder.append(arg);
/* 1203:     */   }
/* 1204:     */   
/* 1205:     */   public void setEditor()
/* 1206:     */   {
/* 1207:1037 */     this.editPane.setText(this.docStringBuilder.toString());
/* 1208:1038 */     this.clearButton.setSelected(false);
/* 1209:1039 */     this.editPane.setCaretPosition(this.editPane.getDocument().getLength());
/* 1210:     */   }
/* 1211:     */   
/* 1212:     */   public Document getDocument()
/* 1213:     */   {
/* 1214:1043 */     return this.editPane.getDocument();
/* 1215:     */   }
/* 1216:     */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.LE8
 * JD-Core Version:    0.7.0.1
 */