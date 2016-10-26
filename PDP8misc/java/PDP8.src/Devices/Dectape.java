/*    1:     */ package Devices;
/*    2:     */ 
/*    3:     */ import Logic.BusRegMem;
/*    4:     */ import Logic.Constants;
/*    5:     */ import Logic.TD8E;
/*    6:     */ import java.awt.Color;
/*    7:     */ import java.awt.Container;
/*    8:     */ import java.awt.Dimension;
/*    9:     */ import java.awt.Font;
/*   10:     */ import java.awt.GridBagConstraints;
/*   11:     */ import java.awt.GridBagLayout;
/*   12:     */ import java.awt.event.ActionEvent;
/*   13:     */ import java.awt.event.ActionListener;
/*   14:     */ import java.awt.event.MouseEvent;
/*   15:     */ import java.awt.event.MouseListener;
/*   16:     */ import java.awt.event.WindowAdapter;
/*   17:     */ import java.awt.event.WindowEvent;
/*   18:     */ import java.beans.PropertyChangeEvent;
/*   19:     */ import java.beans.PropertyChangeListener;
/*   20:     */ import java.io.File;
/*   21:     */ import java.io.IOException;
/*   22:     */ import java.io.PrintStream;
/*   23:     */ import java.io.RandomAccessFile;
/*   24:     */ import javax.swing.AbstractButton;
/*   25:     */ import javax.swing.BorderFactory;
/*   26:     */ import javax.swing.ButtonGroup;
/*   27:     */ import javax.swing.JFileChooser;
/*   28:     */ import javax.swing.JFrame;
/*   29:     */ import javax.swing.JLabel;
/*   30:     */ import javax.swing.JMenu;
/*   31:     */ import javax.swing.JMenuItem;
/*   32:     */ import javax.swing.JOptionPane;
/*   33:     */ import javax.swing.JPanel;
/*   34:     */ import javax.swing.JPopupMenu;
/*   35:     */ import javax.swing.JRadioButtonMenuItem;
/*   36:     */ import javax.swing.border.LineBorder;
/*   37:     */ import javax.swing.filechooser.FileFilter;
/*   38:     */ 
/*   39:     */ public class Dectape
/*   40:     */   extends JFrame
/*   41:     */   implements Constants, MouseListener, ActionListener
/*   42:     */ {
/*   43:     */   private DTReel dTReel1;
/*   44:     */   private DTReel dTReel2;
/*   45:     */   private DTReel dTReel3;
/*   46:     */   private DTReel dTReel4;
/*   47:     */   private JLabel jLabel10;
/*   48:     */   private JLabel jLabel11;
/*   49:     */   private JLabel jLabel13;
/*   50:     */   private JLabel jLabel14;
/*   51:     */   private JLabel jLabel15;
/*   52:     */   private JLabel jLabel16;
/*   53:     */   private JLabel jLabel17;
/*   54:     */   private JLabel jLabel18;
/*   55:     */   private JLabel jLabel2;
/*   56:     */   private JLabel jLabel20;
/*   57:     */   private JLabel jLabel21;
/*   58:     */   private JLabel jLabel22;
/*   59:     */   private JLabel jLabel23;
/*   60:     */   private JLabel jLabel25;
/*   61:     */   private JLabel jLabel3;
/*   62:     */   private JLabel jLabel4;
/*   63:     */   private JLabel jLabel5;
/*   64:     */   private JLabel jLabel6;
/*   65:     */   private JLabel jLabel7;
/*   66:     */   private JLabel jLabel9;
/*   67:     */   private JPanel jPanel1;
/*   68:     */   private JPanel jPanel2;
/*   69:     */   private JPanel jPanel3;
/*   70:     */   private JPopupMenu jPopupMenu1;
/*   71:     */   private DTSwitch mvSwitch0;
/*   72:     */   private DTSwitch mvSwitch1;
/*   73:     */   private DTSwitch rlSwitch0;
/*   74:     */   private DTSwitch rlSwitch1;
/*   75:     */   private JLabel selLamp0;
/*   76:     */   private JLabel selLamp1;
/*   77:     */   private JLabel wlLamp0;
/*   78:     */   private JLabel wlLamp1;
/*   79:     */   private DTSwitch wlSwitch0;
/*   80:     */   private DTSwitch wlSwitch1;
/*   81:     */   public TD8E td8e;
/*   82:     */   
/*   83:     */   public Dectape(TD8E td8e)
/*   84:     */   {
/*   85:  19 */     this.td8e = td8e;
/*   86:  20 */     setTitle("TU56");
/*   87:  21 */     initComponents();
/*   88:  22 */     this.dTReel1.startDt(this, "dTReel1");
/*   89:  23 */     this.dTReel2.startDt(this, "dTReel2");
/*   90:  24 */     this.dTReel3.startDt(this, "dTReel3");
/*   91:  25 */     this.dTReel4.startDt(this, "dTReel4");
/*   92:  26 */     this.wlSwitch0.addPropertyChangeListener(new PropertyChangeListener()
/*   93:     */     {
/*   94:     */       public void propertyChange(PropertyChangeEvent evt)
/*   95:     */       {
/*   96:  29 */         Dectape.this.dtPropertyChange(evt);
/*   97:     */       }
/*   98:  31 */     });
/*   99:  32 */     this.mvSwitch0.addPropertyChangeListener(new PropertyChangeListener()
/*  100:     */     {
/*  101:     */       public void propertyChange(PropertyChangeEvent evt)
/*  102:     */       {
/*  103:  35 */         Dectape.this.dtPropertyChange(evt);
/*  104:     */       }
/*  105:  37 */     });
/*  106:  38 */     this.rlSwitch0.addPropertyChangeListener(new PropertyChangeListener()
/*  107:     */     {
/*  108:     */       public void propertyChange(PropertyChangeEvent evt)
/*  109:     */       {
/*  110:  41 */         Dectape.this.dtPropertyChange(evt);
/*  111:     */       }
/*  112:  43 */     });
/*  113:  44 */     this.wlSwitch1.addPropertyChangeListener(new PropertyChangeListener()
/*  114:     */     {
/*  115:     */       public void propertyChange(PropertyChangeEvent evt)
/*  116:     */       {
/*  117:  47 */         Dectape.this.dtPropertyChange(evt);
/*  118:     */       }
/*  119:  49 */     });
/*  120:  50 */     this.mvSwitch1.addPropertyChangeListener(new PropertyChangeListener()
/*  121:     */     {
/*  122:     */       public void propertyChange(PropertyChangeEvent evt)
/*  123:     */       {
/*  124:  53 */         Dectape.this.dtPropertyChange(evt);
/*  125:     */       }
/*  126:  55 */     });
/*  127:  56 */     this.rlSwitch1.addPropertyChangeListener(new PropertyChangeListener()
/*  128:     */     {
/*  129:     */       public void propertyChange(PropertyChangeEvent evt)
/*  130:     */       {
/*  131:  59 */         Dectape.this.dtPropertyChange(evt);
/*  132:     */       }
/*  133:  61 */     });
/*  134:  62 */     addMouseListener(this);
/*  135:  63 */     this.junit[0] = new JMenu("Unit 0");
/*  136:  64 */     JMenuItem mount0 = new JMenuItem("Mount tape");
/*  137:  65 */     mount0.addActionListener(this);
/*  138:  66 */     mount0.setName("0");
/*  139:  67 */     this.junit[0].add(mount0);
/*  140:  68 */     JMenuItem unmount0 = new JMenuItem("Remove tape");
/*  141:  69 */     unmount0.addActionListener(this);
/*  142:  70 */     unmount0.setName("1");
/*  143:  71 */     this.junit[0].add(unmount0);
/*  144:  72 */     this.jPopupMenu1.add(this.junit[0]);
/*  145:  73 */     this.junit[1] = new JMenu("Unit 1");
/*  146:  74 */     JMenuItem mount1 = new JMenuItem("Mount tape");
/*  147:  75 */     mount1.addActionListener(this);
/*  148:  76 */     mount1.setName("2");
/*  149:  77 */     this.junit[1].add(mount1);
/*  150:  78 */     JMenuItem unmount1 = new JMenuItem("Remove tape");
/*  151:  79 */     unmount1.addActionListener(this);
/*  152:  80 */     unmount1.setName("3");
/*  153:  81 */     this.junit[1].add(unmount1);
/*  154:  82 */     this.jPopupMenu1.add(this.junit[1]);
/*  155:  83 */     this.jPopupMenu1.addSeparator();
/*  156:  84 */     ButtonGroup group = new ButtonGroup();
/*  157:  85 */     this.nowtm = new JRadioButtonMenuItem("Write Data (WTM off)");
/*  158:  86 */     this.nowtm.addActionListener(this);
/*  159:  87 */     this.nowtm.setName("4");
/*  160:  88 */     this.nowtm.setSelected(true);
/*  161:  89 */     group.add(this.nowtm);
/*  162:  90 */     this.jPopupMenu1.add(this.nowtm);
/*  163:  91 */     JRadioButtonMenuItem wtm1 = new JRadioButtonMenuItem("Format PDP8 (WTM on)");
/*  164:  92 */     wtm1.addActionListener(this);
/*  165:  93 */     wtm1.setName("5");
/*  166:  94 */     group.add(wtm1);
/*  167:  95 */     this.jPopupMenu1.add(wtm1);
/*  168:  96 */     JRadioButtonMenuItem wtm2 = new JRadioButtonMenuItem("Format PDP10 (WTM on)");
/*  169:  97 */     wtm2.addActionListener(this);
/*  170:  98 */     wtm2.setName("6");
/*  171:  99 */     group.add(wtm2);
/*  172: 100 */     this.jPopupMenu1.add(wtm2);
/*  173:     */     
/*  174: 102 */     this.chooser = new JFileChooser();
/*  175: 103 */     this.chooser.addChoosableFileFilter(new ImageFileFilter(null));
/*  176:     */   }
/*  177:     */   
/*  178:     */   private void initComponents()
/*  179:     */   {
/*  180: 115 */     this.jPopupMenu1 = new JPopupMenu();
/*  181: 116 */     this.jPanel2 = new JPanel();
/*  182: 117 */     this.jPanel1 = new JPanel();
/*  183: 118 */     this.wlLamp0 = new JLabel();
/*  184: 119 */     this.rlSwitch0 = new DTSwitch();
/*  185: 120 */     this.jLabel2 = new JLabel();
/*  186: 121 */     this.jLabel3 = new JLabel();
/*  187: 122 */     this.jLabel4 = new JLabel();
/*  188: 123 */     this.jLabel5 = new JLabel();
/*  189: 124 */     this.wlSwitch0 = new DTSwitch();
/*  190: 125 */     this.jLabel6 = new JLabel();
/*  191: 126 */     this.jLabel7 = new JLabel();
/*  192: 127 */     this.mvSwitch0 = new DTSwitch();
/*  193: 128 */     this.selLamp0 = new JLabel();
/*  194: 129 */     this.jLabel9 = new JLabel();
/*  195: 130 */     this.jLabel10 = new JLabel();
/*  196: 131 */     this.jLabel11 = new JLabel();
/*  197: 132 */     this.jPanel3 = new JPanel();
/*  198: 133 */     this.wlLamp1 = new JLabel();
/*  199: 134 */     this.rlSwitch1 = new DTSwitch();
/*  200: 135 */     this.jLabel13 = new JLabel();
/*  201: 136 */     this.jLabel14 = new JLabel();
/*  202: 137 */     this.jLabel15 = new JLabel();
/*  203: 138 */     this.wlSwitch1 = new DTSwitch();
/*  204: 139 */     this.jLabel17 = new JLabel();
/*  205: 140 */     this.jLabel18 = new JLabel();
/*  206: 141 */     this.mvSwitch1 = new DTSwitch();
/*  207: 142 */     this.selLamp1 = new JLabel();
/*  208: 143 */     this.jLabel20 = new JLabel();
/*  209: 144 */     this.jLabel21 = new JLabel();
/*  210: 145 */     this.jLabel22 = new JLabel();
/*  211: 146 */     this.jLabel25 = new JLabel();
/*  212: 147 */     this.jLabel23 = new JLabel();
/*  213: 148 */     this.jLabel16 = new JLabel();
/*  214: 149 */     this.dTReel1 = new DTReel();
/*  215: 150 */     this.dTReel2 = new DTReel();
/*  216: 151 */     this.dTReel3 = new DTReel();
/*  217: 152 */     this.dTReel4 = new DTReel();
/*  218:     */     
/*  219: 154 */     addWindowListener(new WindowAdapter()
/*  220:     */     {
/*  221:     */       public void windowClosing(WindowEvent evt)
/*  222:     */       {
/*  223: 156 */         Dectape.this.exitForm(evt);
/*  224:     */       }
/*  225: 158 */     });
/*  226: 159 */     getContentPane().setLayout(new GridBagLayout());
/*  227:     */     
/*  228: 161 */     this.jPanel2.setBackground(new Color(0, 0, 0));
/*  229: 162 */     this.jPanel2.setBorder(new LineBorder(new Color(255, 255, 255), 8, true));
/*  230: 163 */     this.jPanel2.setMaximumSize(new Dimension(600, 330));
/*  231: 164 */     this.jPanel2.setMinimumSize(new Dimension(300, 165));
/*  232: 165 */     this.jPanel2.setPreferredSize(new Dimension(600, 330));
/*  233: 166 */     this.jPanel2.setLayout(new GridBagLayout());
/*  234:     */     
/*  235: 168 */     this.jPanel1.setBackground(new Color(102, 102, 102));
/*  236: 169 */     this.jPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 11), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 2), BorderFactory.createLineBorder(new Color(0, 0, 0), 7))));
/*  237: 170 */     this.jPanel1.setMaximumSize(new Dimension(280, 88));
/*  238: 171 */     this.jPanel1.setMinimumSize(new Dimension(135, 44));
/*  239: 172 */     this.jPanel1.setPreferredSize(new Dimension(280, 88));
/*  240: 173 */     this.jPanel1.setLayout(new GridBagLayout());
/*  241:     */     
/*  242: 175 */     this.wlLamp0.setBackground(new Color(102, 102, 102));
/*  243: 176 */     this.wlLamp0.setFont(new Font("Lucida Grande", 0, 6));
/*  244: 177 */     this.wlLamp0.setHorizontalAlignment(0);
/*  245: 178 */     this.wlLamp0.setText("Write");
/*  246: 179 */     this.wlLamp0.setToolTipText("Write ");
/*  247: 180 */     this.wlLamp0.setAlignmentX(0.5F);
/*  248: 181 */     this.wlLamp0.setHorizontalTextPosition(0);
/*  249: 182 */     this.wlLamp0.setMaximumSize(new Dimension(16, 48));
/*  250: 183 */     this.wlLamp0.setMinimumSize(new Dimension(8, 24));
/*  251: 184 */     this.wlLamp0.setOpaque(true);
/*  252: 185 */     this.wlLamp0.setPreferredSize(new Dimension(16, 48));
/*  253: 186 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*  254: 187 */     gridBagConstraints.gridheight = 2;
/*  255: 188 */     this.jPanel1.add(this.wlLamp0, gridBagConstraints);
/*  256:     */     
/*  257: 190 */     this.rlSwitch0.setMaximumSize(new Dimension(16, 48));
/*  258: 191 */     this.rlSwitch0.setMinimumSize(new Dimension(8, 24));
/*  259: 192 */     this.rlSwitch0.setName("2");
/*  260: 193 */     this.rlSwitch0.setPos3(true);
/*  261: 194 */     this.rlSwitch0.setPreferredSize(new Dimension(16, 48));
/*  262: 195 */     gridBagConstraints = new GridBagConstraints();
/*  263: 196 */     gridBagConstraints.gridx = 8;
/*  264: 197 */     gridBagConstraints.gridy = 0;
/*  265: 198 */     gridBagConstraints.gridheight = 2;
/*  266: 199 */     this.jPanel1.add(this.rlSwitch0, gridBagConstraints);
/*  267:     */     
/*  268: 201 */     this.jLabel2.setBackground(new Color(0, 0, 0));
/*  269: 202 */     this.jLabel2.setFont(new Font("Arial Unicode MS", 0, 8));
/*  270: 203 */     this.jLabel2.setForeground(new Color(255, 255, 255));
/*  271: 204 */     this.jLabel2.setText(" • Write");
/*  272: 205 */     this.jLabel2.setToolTipText("Write enable");
/*  273: 206 */     this.jLabel2.setVerticalAlignment(1);
/*  274: 207 */     this.jLabel2.setMaximumSize(new Dimension(30, 24));
/*  275: 208 */     this.jLabel2.setMinimumSize(new Dimension(19, 12));
/*  276: 209 */     this.jLabel2.setOpaque(true);
/*  277: 210 */     this.jLabel2.setPreferredSize(new Dimension(38, 24));
/*  278: 211 */     gridBagConstraints = new GridBagConstraints();
/*  279: 212 */     gridBagConstraints.gridx = 3;
/*  280: 213 */     this.jPanel1.add(this.jLabel2, gridBagConstraints);
/*  281:     */     
/*  282: 215 */     this.jLabel3.setBackground(new Color(0, 0, 0));
/*  283: 216 */     this.jLabel3.setFont(new Font("Arial Unicode MS", 0, 8));
/*  284: 217 */     this.jLabel3.setForeground(new Color(255, 255, 255));
/*  285: 218 */     this.jLabel3.setHorizontalAlignment(11);
/*  286: 219 */     this.jLabel3.setText("Local • ");
/*  287: 220 */     this.jLabel3.setVerticalAlignment(3);
/*  288: 221 */     this.jLabel3.setMaximumSize(new Dimension(46, 24));
/*  289: 222 */     this.jLabel3.setMinimumSize(new Dimension(23, 12));
/*  290: 223 */     this.jLabel3.setOpaque(true);
/*  291: 224 */     this.jLabel3.setPreferredSize(new Dimension(46, 24));
/*  292: 225 */     gridBagConstraints = new GridBagConstraints();
/*  293: 226 */     gridBagConstraints.gridx = 7;
/*  294: 227 */     gridBagConstraints.gridy = 1;
/*  295: 228 */     this.jPanel1.add(this.jLabel3, gridBagConstraints);
/*  296:     */     
/*  297: 230 */     this.jLabel4.setBackground(new Color(0, 0, 0));
/*  298: 231 */     this.jLabel4.setMaximumSize(new Dimension(4, 48));
/*  299: 232 */     this.jLabel4.setMinimumSize(new Dimension(2, 24));
/*  300: 233 */     this.jLabel4.setOpaque(true);
/*  301: 234 */     this.jLabel4.setPreferredSize(new Dimension(4, 48));
/*  302: 235 */     gridBagConstraints = new GridBagConstraints();
/*  303: 236 */     gridBagConstraints.gridx = 9;
/*  304: 237 */     gridBagConstraints.gridy = 0;
/*  305: 238 */     gridBagConstraints.gridheight = 2;
/*  306: 239 */     this.jPanel1.add(this.jLabel4, gridBagConstraints);
/*  307:     */     
/*  308: 241 */     this.jLabel5.setBackground(new Color(0, 0, 0));
/*  309: 242 */     this.jLabel5.setForeground(new Color(255, 255, 255));
/*  310: 243 */     this.jLabel5.setHorizontalAlignment(0);
/*  311: 244 */     this.jLabel5.setText("0");
/*  312: 245 */     this.jLabel5.setToolTipText("Unit select 0");
/*  313: 246 */     this.jLabel5.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 9), BorderFactory.createLineBorder(new Color(255, 255, 255))));
/*  314: 247 */     this.jLabel5.setMaximumSize(new Dimension(48, 48));
/*  315: 248 */     this.jLabel5.setMinimumSize(new Dimension(24, 24));
/*  316: 249 */     this.jLabel5.setOpaque(true);
/*  317: 250 */     this.jLabel5.setPreferredSize(new Dimension(48, 48));
/*  318: 251 */     gridBagConstraints = new GridBagConstraints();
/*  319: 252 */     gridBagConstraints.gridx = 4;
/*  320: 253 */     gridBagConstraints.gridy = 0;
/*  321: 254 */     gridBagConstraints.gridheight = 2;
/*  322: 255 */     this.jPanel1.add(this.jLabel5, gridBagConstraints);
/*  323:     */     
/*  324: 257 */     this.wlSwitch0.setMaximumSize(new Dimension(16, 48));
/*  325: 258 */     this.wlSwitch0.setMinimumSize(new Dimension(8, 24));
/*  326: 259 */     this.wlSwitch0.setName("0");
/*  327: 260 */     this.wlSwitch0.setPos(1);
/*  328: 261 */     this.wlSwitch0.setPreferredSize(new Dimension(16, 48));
/*  329: 262 */     gridBagConstraints = new GridBagConstraints();
/*  330: 263 */     gridBagConstraints.gridx = 2;
/*  331: 264 */     gridBagConstraints.gridy = 0;
/*  332: 265 */     gridBagConstraints.gridheight = 2;
/*  333: 266 */     this.jPanel1.add(this.wlSwitch0, gridBagConstraints);
/*  334:     */     
/*  335: 268 */     this.jLabel6.setBackground(new Color(0, 0, 0));
/*  336: 269 */     this.jLabel6.setFont(new Font("Arial Unicode MS", 0, 8));
/*  337: 270 */     this.jLabel6.setForeground(new Color(255, 255, 255));
/*  338: 271 */     this.jLabel6.setHorizontalAlignment(4);
/*  339: 272 */     this.jLabel6.setText("Remote • ");
/*  340: 273 */     this.jLabel6.setVerticalAlignment(1);
/*  341: 274 */     this.jLabel6.setMaximumSize(new Dimension(46, 24));
/*  342: 275 */     this.jLabel6.setMinimumSize(new Dimension(23, 12));
/*  343: 276 */     this.jLabel6.setOpaque(true);
/*  344: 277 */     this.jLabel6.setPreferredSize(new Dimension(46, 24));
/*  345: 278 */     gridBagConstraints = new GridBagConstraints();
/*  346: 279 */     gridBagConstraints.gridx = 7;
/*  347: 280 */     gridBagConstraints.gridy = 0;
/*  348: 281 */     this.jPanel1.add(this.jLabel6, gridBagConstraints);
/*  349:     */     
/*  350: 283 */     this.jLabel7.setBackground(new Color(0, 0, 0));
/*  351: 284 */     this.jLabel7.setFont(new Font("Arial Unicode MS", 0, 8));
/*  352: 285 */     this.jLabel7.setForeground(new Color(255, 255, 255));
/*  353: 286 */     this.jLabel7.setText(" •  Lock");
/*  354: 287 */     this.jLabel7.setToolTipText("Write lock");
/*  355: 288 */     this.jLabel7.setVerticalAlignment(3);
/*  356: 289 */     this.jLabel7.setMaximumSize(new Dimension(30, 24));
/*  357: 290 */     this.jLabel7.setMinimumSize(new Dimension(19, 12));
/*  358: 291 */     this.jLabel7.setOpaque(true);
/*  359: 292 */     this.jLabel7.setPreferredSize(new Dimension(38, 24));
/*  360: 293 */     gridBagConstraints = new GridBagConstraints();
/*  361: 294 */     gridBagConstraints.gridy = 1;
/*  362: 295 */     this.jPanel1.add(this.jLabel7, gridBagConstraints);
/*  363:     */     
/*  364: 297 */     this.mvSwitch0.setMaximumSize(new Dimension(16, 48));
/*  365: 298 */     this.mvSwitch0.setMinimumSize(new Dimension(8, 24));
/*  366: 299 */     this.mvSwitch0.setMomentary(true);
/*  367: 300 */     this.mvSwitch0.setName("1");
/*  368: 301 */     this.mvSwitch0.setPos3(true);
/*  369: 302 */     this.mvSwitch0.setPreferredSize(new Dimension(16, 48));
/*  370: 303 */     gridBagConstraints = new GridBagConstraints();
/*  371: 304 */     gridBagConstraints.gridx = 6;
/*  372: 305 */     gridBagConstraints.gridy = 0;
/*  373: 306 */     gridBagConstraints.gridheight = 2;
/*  374: 307 */     this.jPanel1.add(this.mvSwitch0, gridBagConstraints);
/*  375:     */     
/*  376: 309 */     this.selLamp0.setBackground(new Color(102, 102, 102));
/*  377: 310 */     this.selLamp0.setFont(new Font("Lucida Grande", 0, 6));
/*  378: 311 */     this.selLamp0.setHorizontalAlignment(0);
/*  379: 312 */     this.selLamp0.setText("Select");
/*  380: 313 */     this.selLamp0.setToolTipText("Remote select");
/*  381: 314 */     this.selLamp0.setMaximumSize(new Dimension(16, 48));
/*  382: 315 */     this.selLamp0.setMinimumSize(new Dimension(8, 24));
/*  383: 316 */     this.selLamp0.setOpaque(true);
/*  384: 317 */     this.selLamp0.setPreferredSize(new Dimension(16, 48));
/*  385: 318 */     gridBagConstraints = new GridBagConstraints();
/*  386: 319 */     gridBagConstraints.gridx = 10;
/*  387: 320 */     gridBagConstraints.gridheight = 2;
/*  388: 321 */     this.jPanel1.add(this.selLamp0, gridBagConstraints);
/*  389:     */     
/*  390: 323 */     this.jLabel9.setBackground(new Color(0, 0, 0));
/*  391: 324 */     this.jLabel9.setFont(new Font("Arial Unicode MS", 1, 14));
/*  392: 325 */     this.jLabel9.setForeground(new Color(255, 255, 255));
/*  393: 326 */     this.jLabel9.setHorizontalAlignment(0);
/*  394: 327 */     this.jLabel9.setText("←");
/*  395: 328 */     this.jLabel9.setToolTipText("Reverse");
/*  396: 329 */     this.jLabel9.setVerticalAlignment(3);
/*  397: 330 */     this.jLabel9.setMaximumSize(new Dimension(20, 24));
/*  398: 331 */     this.jLabel9.setMinimumSize(new Dimension(10, 12));
/*  399: 332 */     this.jLabel9.setOpaque(true);
/*  400: 333 */     this.jLabel9.setPreferredSize(new Dimension(20, 24));
/*  401: 334 */     gridBagConstraints = new GridBagConstraints();
/*  402: 335 */     gridBagConstraints.gridx = 5;
/*  403: 336 */     gridBagConstraints.gridy = 1;
/*  404: 337 */     this.jPanel1.add(this.jLabel9, gridBagConstraints);
/*  405:     */     
/*  406: 339 */     this.jLabel10.setBackground(new Color(0, 0, 0));
/*  407: 340 */     this.jLabel10.setMaximumSize(new Dimension(4, 48));
/*  408: 341 */     this.jLabel10.setMinimumSize(new Dimension(2, 24));
/*  409: 342 */     this.jLabel10.setOpaque(true);
/*  410: 343 */     this.jLabel10.setPreferredSize(new Dimension(4, 48));
/*  411: 344 */     gridBagConstraints = new GridBagConstraints();
/*  412: 345 */     gridBagConstraints.gridx = 1;
/*  413: 346 */     gridBagConstraints.gridy = 0;
/*  414: 347 */     gridBagConstraints.gridheight = 2;
/*  415: 348 */     this.jPanel1.add(this.jLabel10, gridBagConstraints);
/*  416:     */     
/*  417: 350 */     this.jLabel11.setBackground(new Color(0, 0, 0));
/*  418: 351 */     this.jLabel11.setFont(new Font("Arial Unicode MS", 1, 14));
/*  419: 352 */     this.jLabel11.setForeground(new Color(255, 255, 255));
/*  420: 353 */     this.jLabel11.setHorizontalAlignment(0);
/*  421: 354 */     this.jLabel11.setText("→");
/*  422: 355 */     this.jLabel11.setToolTipText("Forward");
/*  423: 356 */     this.jLabel11.setVerticalAlignment(1);
/*  424: 357 */     this.jLabel11.setMaximumSize(new Dimension(20, 24));
/*  425: 358 */     this.jLabel11.setMinimumSize(new Dimension(10, 12));
/*  426: 359 */     this.jLabel11.setOpaque(true);
/*  427: 360 */     this.jLabel11.setPreferredSize(new Dimension(20, 24));
/*  428: 361 */     gridBagConstraints = new GridBagConstraints();
/*  429: 362 */     gridBagConstraints.gridx = 5;
/*  430: 363 */     gridBagConstraints.gridy = 0;
/*  431: 364 */     this.jPanel1.add(this.jLabel11, gridBagConstraints);
/*  432:     */     
/*  433: 366 */     gridBagConstraints = new GridBagConstraints();
/*  434: 367 */     gridBagConstraints.gridx = 0;
/*  435: 368 */     gridBagConstraints.gridy = 0;
/*  436: 369 */     gridBagConstraints.gridwidth = 2;
/*  437: 370 */     this.jPanel2.add(this.jPanel1, gridBagConstraints);
/*  438:     */     
/*  439: 372 */     this.jPanel3.setBackground(new Color(102, 102, 102));
/*  440: 373 */     this.jPanel3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 11), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 2), BorderFactory.createLineBorder(new Color(0, 0, 0), 7))));
/*  441: 374 */     this.jPanel3.setMaximumSize(new Dimension(280, 88));
/*  442: 375 */     this.jPanel3.setMinimumSize(new Dimension(135, 44));
/*  443: 376 */     this.jPanel3.setPreferredSize(new Dimension(280, 88));
/*  444: 377 */     this.jPanel3.setLayout(new GridBagLayout());
/*  445:     */     
/*  446: 379 */     this.wlLamp1.setBackground(new Color(102, 102, 102));
/*  447: 380 */     this.wlLamp1.setFont(new Font("Lucida Grande", 0, 6));
/*  448: 381 */     this.wlLamp1.setHorizontalAlignment(0);
/*  449: 382 */     this.wlLamp1.setText("Write");
/*  450: 383 */     this.wlLamp1.setToolTipText("Write");
/*  451: 384 */     this.wlLamp1.setMaximumSize(new Dimension(16, 48));
/*  452: 385 */     this.wlLamp1.setMinimumSize(new Dimension(8, 24));
/*  453: 386 */     this.wlLamp1.setOpaque(true);
/*  454: 387 */     this.wlLamp1.setPreferredSize(new Dimension(16, 48));
/*  455: 388 */     gridBagConstraints = new GridBagConstraints();
/*  456: 389 */     gridBagConstraints.gridheight = 2;
/*  457: 390 */     this.jPanel3.add(this.wlLamp1, gridBagConstraints);
/*  458:     */     
/*  459: 392 */     this.rlSwitch1.setMaximumSize(new Dimension(16, 48));
/*  460: 393 */     this.rlSwitch1.setMinimumSize(new Dimension(8, 24));
/*  461: 394 */     this.rlSwitch1.setName("6");
/*  462: 395 */     this.rlSwitch1.setPos3(true);
/*  463: 396 */     this.rlSwitch1.setPreferredSize(new Dimension(16, 48));
/*  464: 397 */     gridBagConstraints = new GridBagConstraints();
/*  465: 398 */     gridBagConstraints.gridx = 8;
/*  466: 399 */     gridBagConstraints.gridy = 0;
/*  467: 400 */     gridBagConstraints.gridheight = 2;
/*  468: 401 */     this.jPanel3.add(this.rlSwitch1, gridBagConstraints);
/*  469:     */     
/*  470: 403 */     this.jLabel13.setBackground(new Color(0, 0, 0));
/*  471: 404 */     this.jLabel13.setFont(new Font("Arial Unicode MS", 0, 8));
/*  472: 405 */     this.jLabel13.setForeground(new Color(255, 255, 255));
/*  473: 406 */     this.jLabel13.setText(" • Write");
/*  474: 407 */     this.jLabel13.setToolTipText("Write enable");
/*  475: 408 */     this.jLabel13.setVerticalAlignment(1);
/*  476: 409 */     this.jLabel13.setMaximumSize(new Dimension(30, 26));
/*  477: 410 */     this.jLabel13.setMinimumSize(new Dimension(19, 12));
/*  478: 411 */     this.jLabel13.setOpaque(true);
/*  479: 412 */     this.jLabel13.setPreferredSize(new Dimension(38, 24));
/*  480: 413 */     gridBagConstraints = new GridBagConstraints();
/*  481: 414 */     gridBagConstraints.gridx = 3;
/*  482: 415 */     this.jPanel3.add(this.jLabel13, gridBagConstraints);
/*  483:     */     
/*  484: 417 */     this.jLabel14.setBackground(new Color(0, 0, 0));
/*  485: 418 */     this.jLabel14.setFont(new Font("Arial Unicode MS", 0, 8));
/*  486: 419 */     this.jLabel14.setForeground(new Color(255, 255, 255));
/*  487: 420 */     this.jLabel14.setHorizontalAlignment(11);
/*  488: 421 */     this.jLabel14.setText("Local • ");
/*  489: 422 */     this.jLabel14.setVerticalAlignment(3);
/*  490: 423 */     this.jLabel14.setMaximumSize(new Dimension(46, 24));
/*  491: 424 */     this.jLabel14.setMinimumSize(new Dimension(23, 12));
/*  492: 425 */     this.jLabel14.setOpaque(true);
/*  493: 426 */     this.jLabel14.setPreferredSize(new Dimension(46, 24));
/*  494: 427 */     gridBagConstraints = new GridBagConstraints();
/*  495: 428 */     gridBagConstraints.gridx = 7;
/*  496: 429 */     gridBagConstraints.gridy = 1;
/*  497: 430 */     this.jPanel3.add(this.jLabel14, gridBagConstraints);
/*  498:     */     
/*  499: 432 */     this.jLabel15.setBackground(new Color(0, 0, 0));
/*  500: 433 */     this.jLabel15.setMaximumSize(new Dimension(4, 48));
/*  501: 434 */     this.jLabel15.setMinimumSize(new Dimension(2, 24));
/*  502: 435 */     this.jLabel15.setOpaque(true);
/*  503: 436 */     this.jLabel15.setPreferredSize(new Dimension(4, 48));
/*  504: 437 */     gridBagConstraints = new GridBagConstraints();
/*  505: 438 */     gridBagConstraints.gridx = 9;
/*  506: 439 */     gridBagConstraints.gridy = 0;
/*  507: 440 */     gridBagConstraints.gridheight = 2;
/*  508: 441 */     this.jPanel3.add(this.jLabel15, gridBagConstraints);
/*  509:     */     
/*  510: 443 */     this.wlSwitch1.setMaximumSize(new Dimension(16, 48));
/*  511: 444 */     this.wlSwitch1.setMinimumSize(new Dimension(8, 24));
/*  512: 445 */     this.wlSwitch1.setName("4");
/*  513: 446 */     this.wlSwitch1.setPos(1);
/*  514: 447 */     this.wlSwitch1.setPreferredSize(new Dimension(16, 48));
/*  515: 448 */     gridBagConstraints = new GridBagConstraints();
/*  516: 449 */     gridBagConstraints.gridx = 2;
/*  517: 450 */     gridBagConstraints.gridy = 0;
/*  518: 451 */     gridBagConstraints.gridheight = 2;
/*  519: 452 */     this.jPanel3.add(this.wlSwitch1, gridBagConstraints);
/*  520:     */     
/*  521: 454 */     this.jLabel17.setBackground(new Color(0, 0, 0));
/*  522: 455 */     this.jLabel17.setFont(new Font("Arial Unicode MS", 0, 8));
/*  523: 456 */     this.jLabel17.setForeground(new Color(255, 255, 255));
/*  524: 457 */     this.jLabel17.setHorizontalAlignment(4);
/*  525: 458 */     this.jLabel17.setText("Remote • ");
/*  526: 459 */     this.jLabel17.setVerticalAlignment(1);
/*  527: 460 */     this.jLabel17.setMaximumSize(new Dimension(46, 24));
/*  528: 461 */     this.jLabel17.setMinimumSize(new Dimension(23, 12));
/*  529: 462 */     this.jLabel17.setOpaque(true);
/*  530: 463 */     this.jLabel17.setPreferredSize(new Dimension(46, 24));
/*  531: 464 */     gridBagConstraints = new GridBagConstraints();
/*  532: 465 */     gridBagConstraints.gridx = 7;
/*  533: 466 */     gridBagConstraints.gridy = 0;
/*  534: 467 */     this.jPanel3.add(this.jLabel17, gridBagConstraints);
/*  535:     */     
/*  536: 469 */     this.jLabel18.setBackground(new Color(0, 0, 0));
/*  537: 470 */     this.jLabel18.setFont(new Font("Arial Unicode MS", 0, 8));
/*  538: 471 */     this.jLabel18.setForeground(new Color(255, 255, 255));
/*  539: 472 */     this.jLabel18.setText(" • Lock");
/*  540: 473 */     this.jLabel18.setToolTipText("Write lock");
/*  541: 474 */     this.jLabel18.setVerticalAlignment(3);
/*  542: 475 */     this.jLabel18.setMaximumSize(new Dimension(38, 24));
/*  543: 476 */     this.jLabel18.setMinimumSize(new Dimension(19, 12));
/*  544: 477 */     this.jLabel18.setOpaque(true);
/*  545: 478 */     this.jLabel18.setPreferredSize(new Dimension(38, 24));
/*  546: 479 */     gridBagConstraints = new GridBagConstraints();
/*  547: 480 */     gridBagConstraints.gridy = 1;
/*  548: 481 */     this.jPanel3.add(this.jLabel18, gridBagConstraints);
/*  549:     */     
/*  550: 483 */     this.mvSwitch1.setMaximumSize(new Dimension(16, 48));
/*  551: 484 */     this.mvSwitch1.setMinimumSize(new Dimension(8, 24));
/*  552: 485 */     this.mvSwitch1.setMomentary(true);
/*  553: 486 */     this.mvSwitch1.setName("5");
/*  554: 487 */     this.mvSwitch1.setPos3(true);
/*  555: 488 */     this.mvSwitch1.setPreferredSize(new Dimension(16, 48));
/*  556: 489 */     gridBagConstraints = new GridBagConstraints();
/*  557: 490 */     gridBagConstraints.gridx = 6;
/*  558: 491 */     gridBagConstraints.gridy = 0;
/*  559: 492 */     gridBagConstraints.gridheight = 2;
/*  560: 493 */     this.jPanel3.add(this.mvSwitch1, gridBagConstraints);
/*  561:     */     
/*  562: 495 */     this.selLamp1.setBackground(new Color(102, 102, 102));
/*  563: 496 */     this.selLamp1.setFont(new Font("Lucida Grande", 0, 6));
/*  564: 497 */     this.selLamp1.setHorizontalAlignment(0);
/*  565: 498 */     this.selLamp1.setText("Select");
/*  566: 499 */     this.selLamp1.setToolTipText("Remote select");
/*  567: 500 */     this.selLamp1.setMaximumSize(new Dimension(16, 48));
/*  568: 501 */     this.selLamp1.setMinimumSize(new Dimension(8, 24));
/*  569: 502 */     this.selLamp1.setOpaque(true);
/*  570: 503 */     this.selLamp1.setPreferredSize(new Dimension(16, 48));
/*  571: 504 */     gridBagConstraints = new GridBagConstraints();
/*  572: 505 */     gridBagConstraints.gridx = 10;
/*  573: 506 */     gridBagConstraints.gridheight = 2;
/*  574: 507 */     this.jPanel3.add(this.selLamp1, gridBagConstraints);
/*  575:     */     
/*  576: 509 */     this.jLabel20.setBackground(new Color(0, 0, 0));
/*  577: 510 */     this.jLabel20.setFont(new Font("Arial Unicode MS", 1, 14));
/*  578: 511 */     this.jLabel20.setForeground(new Color(255, 255, 255));
/*  579: 512 */     this.jLabel20.setHorizontalAlignment(0);
/*  580: 513 */     this.jLabel20.setText("←");
/*  581: 514 */     this.jLabel20.setToolTipText("Reverse");
/*  582: 515 */     this.jLabel20.setVerticalAlignment(3);
/*  583: 516 */     this.jLabel20.setMaximumSize(new Dimension(4, 24));
/*  584: 517 */     this.jLabel20.setMinimumSize(new Dimension(2, 12));
/*  585: 518 */     this.jLabel20.setOpaque(true);
/*  586: 519 */     this.jLabel20.setPreferredSize(new Dimension(20, 24));
/*  587: 520 */     gridBagConstraints = new GridBagConstraints();
/*  588: 521 */     gridBagConstraints.gridx = 5;
/*  589: 522 */     gridBagConstraints.gridy = 1;
/*  590: 523 */     this.jPanel3.add(this.jLabel20, gridBagConstraints);
/*  591:     */     
/*  592: 525 */     this.jLabel21.setBackground(new Color(0, 0, 0));
/*  593: 526 */     this.jLabel21.setMaximumSize(new Dimension(4, 48));
/*  594: 527 */     this.jLabel21.setMinimumSize(new Dimension(2, 24));
/*  595: 528 */     this.jLabel21.setOpaque(true);
/*  596: 529 */     this.jLabel21.setPreferredSize(new Dimension(4, 48));
/*  597: 530 */     gridBagConstraints = new GridBagConstraints();
/*  598: 531 */     gridBagConstraints.gridx = 1;
/*  599: 532 */     gridBagConstraints.gridy = 0;
/*  600: 533 */     gridBagConstraints.gridheight = 2;
/*  601: 534 */     this.jPanel3.add(this.jLabel21, gridBagConstraints);
/*  602:     */     
/*  603: 536 */     this.jLabel22.setBackground(new Color(0, 0, 0));
/*  604: 537 */     this.jLabel22.setFont(new Font("Arial Unicode MS", 1, 14));
/*  605: 538 */     this.jLabel22.setForeground(new Color(255, 255, 255));
/*  606: 539 */     this.jLabel22.setHorizontalAlignment(0);
/*  607: 540 */     this.jLabel22.setText("→");
/*  608: 541 */     this.jLabel22.setToolTipText("Forward");
/*  609: 542 */     this.jLabel22.setVerticalAlignment(1);
/*  610: 543 */     this.jLabel22.setMaximumSize(new Dimension(4, 24));
/*  611: 544 */     this.jLabel22.setMinimumSize(new Dimension(2, 12));
/*  612: 545 */     this.jLabel22.setOpaque(true);
/*  613: 546 */     this.jLabel22.setPreferredSize(new Dimension(20, 24));
/*  614: 547 */     gridBagConstraints = new GridBagConstraints();
/*  615: 548 */     gridBagConstraints.gridx = 5;
/*  616: 549 */     gridBagConstraints.gridy = 0;
/*  617: 550 */     this.jPanel3.add(this.jLabel22, gridBagConstraints);
/*  618:     */     
/*  619: 552 */     this.jLabel25.setBackground(new Color(0, 0, 0));
/*  620: 553 */     this.jLabel25.setForeground(new Color(255, 255, 255));
/*  621: 554 */     this.jLabel25.setHorizontalAlignment(0);
/*  622: 555 */     this.jLabel25.setText("1");
/*  623: 556 */     this.jLabel25.setToolTipText("Unit select 1");
/*  624: 557 */     this.jLabel25.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 9), BorderFactory.createLineBorder(new Color(255, 255, 255))));
/*  625: 558 */     this.jLabel25.setMaximumSize(new Dimension(50, 52));
/*  626: 559 */     this.jLabel25.setMinimumSize(new Dimension(24, 24));
/*  627: 560 */     this.jLabel25.setOpaque(true);
/*  628: 561 */     this.jLabel25.setPreferredSize(new Dimension(48, 48));
/*  629: 562 */     gridBagConstraints = new GridBagConstraints();
/*  630: 563 */     gridBagConstraints.gridx = 4;
/*  631: 564 */     gridBagConstraints.gridy = 0;
/*  632: 565 */     gridBagConstraints.gridheight = 2;
/*  633: 566 */     this.jPanel3.add(this.jLabel25, gridBagConstraints);
/*  634:     */     
/*  635: 568 */     gridBagConstraints = new GridBagConstraints();
/*  636: 569 */     gridBagConstraints.gridx = 3;
/*  637: 570 */     gridBagConstraints.gridy = 0;
/*  638: 571 */     gridBagConstraints.gridwidth = 2;
/*  639: 572 */     this.jPanel2.add(this.jPanel3, gridBagConstraints);
/*  640:     */     
/*  641: 574 */     this.jLabel23.setBackground(new Color(0, 0, 0));
/*  642: 575 */     this.jLabel23.setMaximumSize(new Dimension(22, 224));
/*  643: 576 */     this.jLabel23.setMinimumSize(new Dimension(10, 104));
/*  644: 577 */     this.jLabel23.setOpaque(true);
/*  645: 578 */     this.jLabel23.setPreferredSize(new Dimension(22, 224));
/*  646: 579 */     gridBagConstraints = new GridBagConstraints();
/*  647: 580 */     gridBagConstraints.gridx = 2;
/*  648: 581 */     gridBagConstraints.gridy = 1;
/*  649: 582 */     this.jPanel2.add(this.jLabel23, gridBagConstraints);
/*  650:     */     
/*  651: 584 */     this.jLabel16.setBackground(new Color(0, 0, 0));
/*  652: 585 */     this.jLabel16.setForeground(new Color(255, 255, 255));
/*  653: 586 */     this.jLabel16.setHorizontalAlignment(0);
/*  654: 587 */     this.jLabel16.setMaximumSize(new Dimension(50, 88));
/*  655: 588 */     this.jLabel16.setMinimumSize(new Dimension(11, 44));
/*  656: 589 */     this.jLabel16.setOpaque(true);
/*  657: 590 */     this.jLabel16.setPreferredSize(new Dimension(22, 88));
/*  658: 591 */     gridBagConstraints = new GridBagConstraints();
/*  659: 592 */     gridBagConstraints.gridx = 2;
/*  660: 593 */     gridBagConstraints.gridy = 0;
/*  661: 594 */     this.jPanel2.add(this.jLabel16, gridBagConstraints);
/*  662:     */     
/*  663: 596 */     this.dTReel1.setBackground(new Color(0, 0, 0));
/*  664: 597 */     this.dTReel1.setMaximumSize(new Dimension(140, 224));
/*  665: 598 */     gridBagConstraints = new GridBagConstraints();
/*  666: 599 */     gridBagConstraints.gridx = 0;
/*  667: 600 */     gridBagConstraints.gridy = 1;
/*  668: 601 */     this.jPanel2.add(this.dTReel1, gridBagConstraints);
/*  669:     */     
/*  670: 603 */     this.dTReel2.setBackground(new Color(0, 0, 0));
/*  671: 604 */     this.dTReel2.setMaximumSize(new Dimension(140, 224));
/*  672: 605 */     this.dTReel2.setRight(true);
/*  673: 606 */     gridBagConstraints = new GridBagConstraints();
/*  674: 607 */     gridBagConstraints.gridx = 1;
/*  675: 608 */     gridBagConstraints.gridy = 1;
/*  676: 609 */     this.jPanel2.add(this.dTReel2, gridBagConstraints);
/*  677:     */     
/*  678: 611 */     this.dTReel3.setBackground(new Color(0, 0, 0));
/*  679: 612 */     this.dTReel3.setMaximumSize(new Dimension(140, 224));
/*  680: 613 */     this.dTReel3.setUnit(1);
/*  681: 614 */     gridBagConstraints = new GridBagConstraints();
/*  682: 615 */     gridBagConstraints.gridx = 3;
/*  683: 616 */     gridBagConstraints.gridy = 1;
/*  684: 617 */     this.jPanel2.add(this.dTReel3, gridBagConstraints);
/*  685:     */     
/*  686: 619 */     this.dTReel4.setBackground(new Color(0, 0, 0));
/*  687: 620 */     this.dTReel4.setMaximumSize(new Dimension(140, 224));
/*  688: 621 */     this.dTReel4.setRight(true);
/*  689: 622 */     this.dTReel4.setUnit(1);
/*  690: 623 */     gridBagConstraints = new GridBagConstraints();
/*  691: 624 */     gridBagConstraints.gridx = 4;
/*  692: 625 */     gridBagConstraints.gridy = 1;
/*  693: 626 */     this.jPanel2.add(this.dTReel4, gridBagConstraints);
/*  694:     */     
/*  695: 628 */     getContentPane().add(this.jPanel2, new GridBagConstraints());
/*  696:     */     
/*  697: 630 */     pack();
/*  698:     */   }
/*  699:     */   
/*  700:     */   private void exitForm(WindowEvent evt)
/*  701:     */   {
/*  702: 635 */     this.td8e.data.CloseAllDevs();
/*  703:     */   }
/*  704:     */   
/*  705:     */   private void dtPropertyChange(PropertyChangeEvent evt)
/*  706:     */   {
/*  707: 639 */     Integer objswr = (Integer)evt.getNewValue();
/*  708: 640 */     int val = objswr.intValue();
/*  709: 641 */     int mask = Integer.parseInt(evt.getPropertyName());
/*  710: 642 */     int unit = (mask & 0x4) >> 2;
/*  711: 643 */     switch (mask & 0x3)
/*  712:     */     {
/*  713:     */     case 0: 
/*  714: 645 */       writeLock(unit, val);
/*  715: 646 */       break;
/*  716:     */     case 1: 
/*  717: 648 */       moveTape(unit, val);
/*  718: 649 */       break;
/*  719:     */     case 2: 
/*  720: 651 */       selTape(unit, val);
/*  721:     */     }
/*  722:     */   }
/*  723:     */   
/*  724:     */   private void writeLock(int unit, int val)
/*  725:     */   {
/*  726: 657 */     if (((val == 2 ? 1 : 0) & (this.real_wlo[unit] == 0 ? 1 : 0)) != 0)
/*  727:     */     {
/*  728: 658 */       this.td8e.wlo[unit] = false;
/*  729: 659 */       if (unit == 0) {
/*  730: 660 */         this.wlLamp0.setBackground(new Color(204, 204, 0));
/*  731:     */       } else {
/*  732: 662 */         this.wlLamp1.setBackground(new Color(204, 204, 0));
/*  733:     */       }
/*  734:     */     }
/*  735:     */     else
/*  736:     */     {
/*  737: 665 */       this.td8e.wlo[unit] = true;
/*  738: 666 */       if (unit == 0) {
/*  739: 667 */         this.wlLamp0.setBackground(new Color(102, 102, 102));
/*  740:     */       } else {
/*  741: 669 */         this.wlLamp1.setBackground(new Color(102, 102, 102));
/*  742:     */       }
/*  743:     */     }
/*  744:     */   }
/*  745:     */   
/*  746:     */   private void moveTape(int unit, int val)
/*  747:     */   {
/*  748: 675 */     if (this.local[unit] == 1) {
/*  749: 676 */       switch (val)
/*  750:     */       {
/*  751:     */       case 2: 
/*  752: 678 */         this.direction[unit] = 1;
/*  753: 679 */         break;
/*  754:     */       case 0: 
/*  755: 681 */         this.direction[unit] = 0;
/*  756: 682 */         break;
/*  757:     */       case 1: 
/*  758: 684 */         this.direction[unit] = -1;
/*  759:     */       }
/*  760:     */     }
/*  761:     */   }
/*  762:     */   
/*  763:     */   private void selTape(int unit, int val)
/*  764:     */   {
/*  765: 691 */     if (val == 2)
/*  766:     */     {
/*  767: 692 */       this.td8e.sel[unit] = true;
/*  768: 693 */       this.local[unit] = false;
/*  769:     */     }
/*  770:     */     else
/*  771:     */     {
/*  772: 695 */       this.td8e.sel[unit] = false;
/*  773: 696 */       if (val == 1)
/*  774:     */       {
/*  775: 697 */         this.local[unit] = true;
/*  776: 698 */         if (this.td8e.tape[unit] != null) {
/*  777: 698 */           this.td8e.line[unit] = ((this.tapesize[unit] - 949728 + 49152) / 2);
/*  778:     */         }
/*  779:     */       }
/*  780:     */       else
/*  781:     */       {
/*  782: 700 */         this.local[unit] = false;
/*  783:     */       }
/*  784:     */     }
/*  785:     */   }
/*  786:     */   
/*  787:     */   public void selLamp(int unit)
/*  788:     */   {
/*  789: 706 */     if (((unit == 0 ? 1 : 0) & this.td8e.sel[unit]) != 0) {
/*  790: 707 */       this.selLamp0.setBackground(new Color(204, 204, 0));
/*  791:     */     } else {
/*  792: 709 */       this.selLamp0.setBackground(new Color(102, 102, 102));
/*  793:     */     }
/*  794: 711 */     if (((unit == 1 ? 1 : 0) & this.td8e.sel[unit]) != 0) {
/*  795: 712 */       this.selLamp1.setBackground(new Color(204, 204, 0));
/*  796:     */     } else {
/*  797: 714 */       this.selLamp1.setBackground(new Color(102, 102, 102));
/*  798:     */     }
/*  799:     */   }
/*  800:     */   
/*  801:     */   public void mouseClicked(MouseEvent e)
/*  802:     */   {
/*  803: 719 */     if (e.isPopupTrigger()) {
/*  804: 720 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/*  805:     */     }
/*  806:     */   }
/*  807:     */   
/*  808:     */   public void mouseEntered(MouseEvent e) {}
/*  809:     */   
/*  810:     */   public void mouseExited(MouseEvent e) {}
/*  811:     */   
/*  812:     */   public void mousePressed(MouseEvent e)
/*  813:     */   {
/*  814: 731 */     if (e.isPopupTrigger()) {
/*  815: 732 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/*  816:     */     }
/*  817:     */   }
/*  818:     */   
/*  819:     */   public void mouseReleased(MouseEvent e)
/*  820:     */   {
/*  821: 737 */     if (e.isPopupTrigger()) {
/*  822: 738 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/*  823:     */     }
/*  824:     */   }
/*  825:     */   
/*  826:     */   public void actionPerformed(ActionEvent e)
/*  827:     */   {
/*  828: 743 */     AbstractButton x = (AbstractButton)e.getSource();
/*  829: 744 */     Integer val = new Integer(Integer.parseInt(x.getName()));
/*  830: 745 */     switch (val.intValue() & 0x7)
/*  831:     */     {
/*  832:     */     case 0: 
/*  833: 747 */       openTape(0);
/*  834: 748 */       break;
/*  835:     */     case 1: 
/*  836: 750 */       closeTape(0);
/*  837: 751 */       break;
/*  838:     */     case 2: 
/*  839: 753 */       openTape(1);
/*  840: 754 */       break;
/*  841:     */     case 3: 
/*  842: 756 */       closeTape(1);
/*  843: 757 */       break;
/*  844:     */     case 4: 
/*  845: 759 */       this.td8e.setWtm(false);
/*  846: 760 */       break;
/*  847:     */     case 5: 
/*  848: 762 */       setFormat(true);
/*  849: 763 */       break;
/*  850:     */     case 6: 
/*  851: 765 */       setFormat(false);
/*  852:     */     }
/*  853: 768 */     repaint();
/*  854:     */   }
/*  855:     */   
/*  856:     */   private void setFormat(boolean wtm8)
/*  857:     */   {
/*  858: 772 */     if (((this.td8e.tape[0] == null ? 1 : 0) | (this.td8e.tape[1] != null ? 1 : 0)) != 0)
/*  859:     */     {
/*  860: 773 */       JOptionPane.showMessageDialog(this, "Unit 0 must have file to format and Unit 1 tape removed!", "Format", 2);
/*  861:     */       
/*  862:     */ 
/*  863:     */ 
/*  864: 777 */       this.nowtm.setSelected(true);
/*  865: 778 */       return;
/*  866:     */     }
/*  867: 780 */     boolean test = true;
/*  868:     */     try
/*  869:     */     {
/*  870: 782 */       this.td8e.tape[0].seek(0L);
/*  871: 783 */       if (this.td8e.tape[0].read() != 116) {
/*  872: 784 */         test = false;
/*  873:     */       }
/*  874: 786 */       if (this.td8e.tape[0].read() != 117) {
/*  875: 787 */         test = false;
/*  876:     */       }
/*  877: 789 */       if (this.td8e.tape[0].read() != 53) {
/*  878: 790 */         test = false;
/*  879:     */       }
/*  880: 792 */       if (this.td8e.tape[0].read() != 54) {
/*  881: 793 */         test = false;
/*  882:     */       }
/*  883: 795 */       if (test)
/*  884:     */       {
/*  885: 796 */         int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to reformat an old file?", "Reformat file?", 1, 2);
/*  886: 801 */         if (result > 0)
/*  887:     */         {
/*  888: 802 */           this.nowtm.setSelected(true);
/*  889: 803 */           return;
/*  890:     */         }
/*  891:     */       }
/*  892: 806 */       this.td8e.tape[0].seek(0L);
/*  893: 807 */       this.td8e.tape[0].write(116);
/*  894: 808 */       this.td8e.tape[0].write(117);
/*  895: 809 */       this.td8e.tape[0].write(53);
/*  896: 810 */       this.td8e.tape[0].write(54);
/*  897: 811 */       if (wtm8)
/*  898:     */       {
/*  899: 812 */         this.td8e.tape[0].write(0);
/*  900: 813 */         this.td8e.tape[0].write(0);
/*  901: 814 */         this.td8e.tape[0].write(0);
/*  902:     */       }
/*  903:     */       else
/*  904:     */       {
/*  905: 816 */         this.td8e.tape[0].write(45);
/*  906: 817 */         this.td8e.tape[0].write(49);
/*  907: 818 */         this.td8e.tape[0].write(48);
/*  908:     */       }
/*  909: 820 */       this.td8e.tape[0].seek(524287L);
/*  910: 821 */       this.tapesize[0] = 1048576;
/*  911: 822 */       this.td8e.tape[0].write(0);
/*  912: 823 */       if (wtm8) {
/*  913: 824 */         this.td8e.line[0] = 49424;
/*  914:     */       } else {
/*  915: 826 */         this.td8e.line[0] = 12692;
/*  916:     */       }
/*  917: 828 */       this.td8e.setWtm(true);
/*  918:     */     }
/*  919:     */     catch (IOException e)
/*  920:     */     {
/*  921: 830 */       System.out.println(e);
/*  922:     */     }
/*  923:     */   }
/*  924:     */   
/*  925:     */   private void openTape(int unit)
/*  926:     */   {
/*  927: 836 */     String dfile = this.td8e.data.getProp("Dectape-" + unit);
/*  928: 837 */     if (dfile != null) {
/*  929: 838 */       this.chooser.setSelectedFile(new File(dfile));
/*  930:     */     }
/*  931: 840 */     int option = this.chooser.showOpenDialog(this);
/*  932: 841 */     if (option == 0)
/*  933:     */     {
/*  934: 842 */       File file = this.chooser.getSelectedFile();
/*  935: 843 */       if (file != null)
/*  936:     */       {
/*  937: 844 */         closeTape(unit);
/*  938:     */         try
/*  939:     */         {
/*  940: 846 */           this.real_wlo[unit] = false;
/*  941: 847 */           if (file.isFile())
/*  942:     */           {
/*  943: 848 */             if (file.canWrite())
/*  944:     */             {
/*  945: 849 */               this.td8e.tape[unit] = new RandomAccessFile(file, "rw");
/*  946:     */             }
/*  947: 850 */             else if (file.canRead())
/*  948:     */             {
/*  949: 851 */               this.td8e.tape[unit] = new RandomAccessFile(file, "r");
/*  950: 852 */               this.real_wlo[unit] = true;
/*  951:     */             }
/*  952:     */             else
/*  953:     */             {
/*  954: 854 */               System.out.println("File seems to be busy!");
/*  955:     */             }
/*  956:     */           }
/*  957:     */           else {
/*  958: 858 */             this.td8e.tape[unit] = new RandomAccessFile(file, "rw");
/*  959:     */           }
/*  960: 861 */           this.junit[unit].setText("Unit " + unit + ": " + file.getName());
/*  961: 862 */           this.td8e.data.setProp("Dectape-" + unit, file.getCanonicalPath());
/*  962:     */         }
/*  963:     */         catch (IOException e)
/*  964:     */         {
/*  965: 864 */           System.out.println(e);
/*  966:     */         }
/*  967: 867 */         if (this.td8e.tape[unit] != null) {
/*  968: 868 */           if (file.length() == 0L)
/*  969:     */           {
/*  970:     */             try
/*  971:     */             {
/*  972: 870 */               this.td8e.tape[unit].seek(524287L);
/*  973: 871 */               this.tapesize[unit] = 1048576;
/*  974: 872 */               this.td8e.tape[unit].write(0);
/*  975:     */             }
/*  976:     */             catch (IOException e)
/*  977:     */             {
/*  978: 874 */               System.out.println(e);
/*  979:     */             }
/*  980:     */           }
/*  981:     */           else
/*  982:     */           {
/*  983: 877 */             boolean test = true;
/*  984:     */             try
/*  985:     */             {
/*  986: 879 */               if (this.td8e.tape[unit].read() != 116) {
/*  987: 880 */                 test = false;
/*  988:     */               }
/*  989: 883 */               if (this.td8e.tape[unit].read() != 117) {
/*  990: 884 */                 test = false;
/*  991:     */               }
/*  992: 887 */               if (this.td8e.tape[unit].read() != 53) {
/*  993: 888 */                 test = false;
/*  994:     */               }
/*  995: 891 */               if (this.td8e.tape[unit].read() != 54) {
/*  996: 892 */                 test = false;
/*  997:     */               }
/*  998: 895 */               if (test)
/*  999:     */               {
/* 1000: 896 */                 this.tapesize[unit] = ((int)file.length() * 2);
/* 1001: 897 */                 this.td8e.line[unit] = ((this.tapesize[unit] - 949728 + 49152) / 2);
/* 1002: 898 */                 if (this.td8e.tape[unit].read() != 45) {
/* 1003: 899 */                   test = false;
/* 1004:     */                 }
/* 1005: 902 */                 if (this.td8e.tape[unit].read() != 49) {
/* 1006: 903 */                   test = false;
/* 1007:     */                 }
/* 1008: 906 */                 if (this.td8e.tape[unit].read() != 48) {
/* 1009: 907 */                   test = false;
/* 1010:     */                 }
/* 1011: 910 */                 if (test) {
/* 1012: 911 */                   this.td8e.line[unit] = ((this.tapesize[unit] - 1023192 + 49152) / 2);
/* 1013:     */                 }
/* 1014:     */               }
/* 1015:     */               else
/* 1016:     */               {
/* 1017: 915 */                 JOptionPane.showMessageDialog(this, "File not formatted: not tu56 or tu56-10", "Bad file", 2);
/* 1018:     */                 
/* 1019:     */ 
/* 1020:     */ 
/* 1021: 919 */                 closeTape(unit);
/* 1022:     */               }
/* 1023:     */             }
/* 1024:     */             catch (IOException e)
/* 1025:     */             {
/* 1026: 923 */               System.out.println(e);
/* 1027:     */             }
/* 1028:     */           }
/* 1029:     */         }
/* 1030:     */       }
/* 1031:     */       else
/* 1032:     */       {
/* 1033: 929 */         System.out.println("No file selected");
/* 1034:     */       }
/* 1035:     */     }
/* 1036:     */   }
/* 1037:     */   
/* 1038:     */   public void closeTape(int unit)
/* 1039:     */   {
/* 1040: 936 */     if (this.td8e.tape[unit] != null)
/* 1041:     */     {
/* 1042:     */       try
/* 1043:     */       {
/* 1044: 938 */         this.td8e.tape[unit].close();
/* 1045:     */       }
/* 1046:     */       catch (IOException e) {}
/* 1047: 941 */       this.td8e.tape[unit] = null;
/* 1048: 942 */       this.tapesize[unit] = 0;
/* 1049: 943 */       this.td8e.line[unit] = 0;
/* 1050:     */     }
/* 1051: 946 */     this.junit[unit].setText("Unit " + unit);
/* 1052:     */   }
/* 1053:     */   
/* 1054:     */   private static class ImageFileFilter
/* 1055:     */     extends FileFilter
/* 1056:     */   {
/* 1057:     */     public boolean accept(File file)
/* 1058:     */     {
/* 1059: 953 */       if (file == null) {
/* 1060: 954 */         return false;
/* 1061:     */       }
/* 1062: 956 */       return (file.isDirectory()) || (file.getName().toLowerCase().endsWith(".pxg"));
/* 1063:     */     }
/* 1064:     */     
/* 1065:     */     public String getDescription()
/* 1066:     */     {
/* 1067: 960 */       return "Dectape files (*.pxg)";
/* 1068:     */     }
/* 1069:     */   }
/* 1070:     */   
/* 1071:1004 */   public boolean[] local = { false, false };
/* 1072:1005 */   public int[] direction = { 0, 0 };
/* 1073:1006 */   public int[] tapesize = { 0, 0 };
/* 1074:1007 */   public boolean[] real_wlo = { false, false };
/* 1075:     */   JRadioButtonMenuItem nowtm;
/* 1076:1010 */   JMenu[] junit = { null, null };
/* 1077:     */   JFileChooser chooser;
/* 1078:     */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.Dectape
 * JD-Core Version:    0.7.0.1
 */