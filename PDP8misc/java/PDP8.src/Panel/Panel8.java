/*    1:     */ package Panel;
/*    2:     */ 
/*    3:     */ import Logic.PanelLogic;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Dimension;
/*    6:     */ import java.awt.Font;
/*    7:     */ import java.awt.GridBagConstraints;
/*    8:     */ import java.awt.GridBagLayout;
/*    9:     */ import java.awt.event.ActionEvent;
/*   10:     */ import java.awt.event.ActionListener;
/*   11:     */ import java.awt.event.ComponentAdapter;
/*   12:     */ import java.awt.event.ComponentEvent;
/*   13:     */ import java.awt.event.MouseEvent;
/*   14:     */ import java.awt.event.MouseListener;
/*   15:     */ import java.beans.PropertyChangeEvent;
/*   16:     */ import java.beans.PropertyChangeListener;
/*   17:     */ import javax.swing.AbstractButton;
/*   18:     */ import javax.swing.BorderFactory;
/*   19:     */ import javax.swing.ButtonGroup;
/*   20:     */ import javax.swing.JLabel;
/*   21:     */ import javax.swing.JMenu;
/*   22:     */ import javax.swing.JMenuItem;
/*   23:     */ import javax.swing.JPanel;
/*   24:     */ import javax.swing.JPopupMenu;
/*   25:     */ import javax.swing.JRadioButtonMenuItem;
/*   26:     */ 
/*   27:     */ public class Panel8
/*   28:     */   extends JPanel
/*   29:     */   implements MouseListener, ActionListener
/*   30:     */ {
/*   31:     */   boolean dummy;
/*   32:     */   public PanelLogic panelLogic;
/*   33:     */   public AcLamps acLamps;
/*   34:     */   public BusLamps busLamps;
/*   35:     */   public DataLamps dataLamps;
/*   36:     */   public EmaLamps emaLamps;
/*   37:     */   private JLabel fill1;
/*   38:     */   private JLabel fill2l;
/*   39:     */   private JLabel fill2r;
/*   40:     */   private JLabel fill4Ma0_2;
/*   41:     */   private JLabel fill4Ma3_4;
/*   42:     */   private JLabel fill4Ma6_8;
/*   43:     */   private JLabel fill4Ma9_11;
/*   44:     */   private JLabel fill4l;
/*   45:     */   private JLabel fill4r1;
/*   46:     */   private JLabel fill4r2;
/*   47:     */   private JLabel fill5l;
/*   48:     */   private JLabel fill5r1;
/*   49:     */   private JLabel fill5r2;
/*   50:     */   private JLabel fill6Bus0_2;
/*   51:     */   private JLabel fill6Bus3_5;
/*   52:     */   private JLabel fill6Bus6_8;
/*   53:     */   private JLabel fill6Bus9_11;
/*   54:     */   private JLabel fill6l1;
/*   55:     */   private JLabel fill6l2;
/*   56:     */   private JLabel fill6r1;
/*   57:     */   private JLabel fill6r2;
/*   58:     */   private JLabel fill6r3;
/*   59:     */   private JLabel fill6rbis;
/*   60:     */   private JLabel fill7;
/*   61:     */   private JLabel fill8l1;
/*   62:     */   private JLabel fill8l2;
/*   63:     */   private JLabel fill8l3;
/*   64:     */   private JLabel fill8r1;
/*   65:     */   private JLabel fill8r2;
/*   66:     */   private JLabel fill8r3;
/*   67:     */   private JLabel fill8r4;
/*   68:     */   private JLabel fill9;
/*   69:     */   private JPopupMenu jPopupMenu1;
/*   70:     */   private Knob knobPower;
/*   71:     */   private Knob knobSelect;
/*   72:     */   public MaLamps maLamps;
/*   73:     */   public MdLamps mdLamps;
/*   74:     */   public MqLamps mqLamps;
/*   75:     */   private JPanel panel6Selector;
/*   76:     */   private JPanel panel8Switches;
/*   77:     */   private JPanel panelAlEal;
/*   78:     */   private JPanel panelDep;
/*   79:     */   private JPanel panelLampsSelector;
/*   80:     */   private JPanel panelPower;
/*   81:     */   private JPanel panelStartExamHaltSS;
/*   82:     */   private JPanel panelSwitch;
/*   83:     */   public Lamp runLamp;
/*   84:     */   public StateLamps stateLamps;
/*   85:     */   public StatusLamps statusLamps;
/*   86:     */   private Switch sw;
/*   87:     */   private Switch swAl;
/*   88:     */   private Switch swClear;
/*   89:     */   private Switch swCont;
/*   90:     */   public Switch swDep;
/*   91:     */   private Switch swEal;
/*   92:     */   private Switch swExam;
/*   93:     */   private Switch swHalt;
/*   94:     */   protected SwitchReg swReg;
/*   95:     */   private Switch swSingStep;
/*   96:     */   private JLabel text2Type;
/*   97:     */   private JLabel text3Company;
/*   98:     */   private JLabel text4Ema;
/*   99:     */   private JLabel text4MemAddr;
/*  100:     */   private JLabel text4Run;
/*  101:     */   private JLabel textAddrLoad;
/*  102:     */   private JLabel textClear;
/*  103:     */   private JLabel textCont;
/*  104:     */   private JLabel textDep;
/*  105:     */   private JLabel textExam;
/*  106:     */   private JLabel textExtdAddrLoad;
/*  107:     */   private JLabel textHalt;
/*  108:     */   private JLabel textLock;
/*  109:     */   private JLabel textOff;
/*  110:     */   private JLabel textPower;
/*  111:     */   private JLabel textRegAc;
/*  112:     */   private JLabel textRegBus;
/*  113:     */   private JLabel textRegMd;
/*  114:     */   private JLabel textRegMq;
/*  115:     */   private JLabel textSingStep;
/*  116:     */   private JLabel textStart;
/*  117:     */   private JLabel textState;
/*  118:     */   private JLabel textStatus;
/*  119:     */   private JLabel textSwitch;
/*  120:     */   JMenu jboot;
/*  121:     */   
/*  122:     */   public Panel8()
/*  123:     */   {
/*  124:  22 */     initComponents();
/*  125:     */     
/*  126:  24 */     this.sw.addPropertyChangeListener(new PropertyChangeListener()
/*  127:     */     {
/*  128:     */       public void propertyChange(PropertyChangeEvent evt)
/*  129:     */       {
/*  130:  26 */         Boolean objswr = (Boolean)evt.getNewValue();
/*  131:  27 */         boolean valswr = objswr.booleanValue();
/*  132:  28 */         Panel8.this.panelLogic.setSw(valswr);
/*  133:     */       }
/*  134:  30 */     });
/*  135:  31 */     this.swEal.addPropertyChangeListener(new PropertyChangeListener()
/*  136:     */     {
/*  137:     */       public void propertyChange(PropertyChangeEvent evt)
/*  138:     */       {
/*  139:  33 */         Boolean objswr = (Boolean)evt.getNewValue();
/*  140:  34 */         boolean valswr = objswr.booleanValue();
/*  141:  35 */         Panel8.this.panelLogic.setEal(valswr);
/*  142:     */       }
/*  143:  37 */     });
/*  144:  38 */     this.swAl.addPropertyChangeListener(new PropertyChangeListener()
/*  145:     */     {
/*  146:     */       public void propertyChange(PropertyChangeEvent evt)
/*  147:     */       {
/*  148:  40 */         Boolean objswr = (Boolean)evt.getNewValue();
/*  149:  41 */         boolean valswr = objswr.booleanValue();
/*  150:  42 */         Panel8.this.panelLogic.setAl(valswr);
/*  151:     */       }
/*  152:  44 */     });
/*  153:  45 */     this.swClear.addPropertyChangeListener(new PropertyChangeListener()
/*  154:     */     {
/*  155:     */       public void propertyChange(PropertyChangeEvent evt)
/*  156:     */       {
/*  157:  47 */         Boolean objswr = (Boolean)evt.getNewValue();
/*  158:  48 */         boolean valswr = objswr.booleanValue();
/*  159:  49 */         Panel8.this.panelLogic.setClear(valswr);
/*  160:     */       }
/*  161:  51 */     });
/*  162:  52 */     this.swCont.addPropertyChangeListener(new PropertyChangeListener()
/*  163:     */     {
/*  164:     */       public void propertyChange(PropertyChangeEvent evt)
/*  165:     */       {
/*  166:  54 */         Boolean objswr = (Boolean)evt.getNewValue();
/*  167:  55 */         boolean valswr = objswr.booleanValue();
/*  168:  56 */         Panel8.this.panelLogic.setCont(valswr);
/*  169:     */       }
/*  170:  58 */     });
/*  171:  59 */     this.swExam.addPropertyChangeListener(new PropertyChangeListener()
/*  172:     */     {
/*  173:     */       public void propertyChange(PropertyChangeEvent evt)
/*  174:     */       {
/*  175:  61 */         Boolean objswr = (Boolean)evt.getNewValue();
/*  176:  62 */         boolean valswr = objswr.booleanValue();
/*  177:  63 */         Panel8.this.panelLogic.setExam(valswr);
/*  178:     */       }
/*  179:  65 */     });
/*  180:  66 */     this.swHalt.addPropertyChangeListener(new PropertyChangeListener()
/*  181:     */     {
/*  182:     */       public void propertyChange(PropertyChangeEvent evt)
/*  183:     */       {
/*  184:  68 */         Boolean objswr = (Boolean)evt.getNewValue();
/*  185:  69 */         boolean valswr = objswr.booleanValue();
/*  186:  70 */         Panel8.this.panelLogic.setHalt(valswr);
/*  187:     */       }
/*  188:  72 */     });
/*  189:  73 */     this.swSingStep.addPropertyChangeListener(new PropertyChangeListener()
/*  190:     */     {
/*  191:     */       public void propertyChange(PropertyChangeEvent evt)
/*  192:     */       {
/*  193:  75 */         Boolean objswr = (Boolean)evt.getNewValue();
/*  194:  76 */         boolean valswr = objswr.booleanValue();
/*  195:  77 */         Panel8.this.panelLogic.setSingStep(valswr);
/*  196:     */       }
/*  197:  79 */     });
/*  198:  80 */     this.swDep.addPropertyChangeListener(new PropertyChangeListener()
/*  199:     */     {
/*  200:     */       public void propertyChange(PropertyChangeEvent evt)
/*  201:     */       {
/*  202:  82 */         Boolean objswr = (Boolean)evt.getNewValue();
/*  203:  83 */         boolean valswr = objswr.booleanValue();
/*  204:  84 */         Panel8.this.panelLogic.setDep(valswr);
/*  205:     */       }
/*  206:  86 */     });
/*  207:  87 */     this.swReg.addPropertyChangeListener(new PropertyChangeListener()
/*  208:     */     {
/*  209:     */       public void propertyChange(PropertyChangeEvent evt)
/*  210:     */       {
/*  211:  89 */         Integer objswr = (Integer)evt.getNewValue();
/*  212:  90 */         int valswr = objswr.intValue();
/*  213:  91 */         Panel8.this.panelLogic.setSwitchReg(valswr);
/*  214:     */       }
/*  215:  93 */     });
/*  216:  94 */     this.knobSelect.addPropertyChangeListener(new PropertyChangeListener()
/*  217:     */     {
/*  218:     */       public void propertyChange(PropertyChangeEvent evt)
/*  219:     */       {
/*  220:  96 */         Integer objswr = (Integer)evt.getNewValue();
/*  221:  97 */         int valswr = objswr.intValue();
/*  222:  98 */         Panel8.this.panelLogic.setSelect(valswr);
/*  223:     */       }
/*  224: 100 */     });
/*  225: 101 */     this.knobPower.addPropertyChangeListener(new PropertyChangeListener()
/*  226:     */     {
/*  227:     */       public void propertyChange(PropertyChangeEvent evt)
/*  228:     */       {
/*  229: 103 */         Integer objswr = (Integer)evt.getNewValue();
/*  230: 104 */         int valswr = objswr.intValue();
/*  231: 105 */         Panel8.this.panelLogic.setPower(valswr);
/*  232:     */       }
/*  233: 107 */     });
/*  234: 108 */     addMouseListener(this);
/*  235: 109 */     this.jboot = new JMenu("Boot");
/*  236: 110 */     JMenuItem si3040 = new JMenuItem("SI3040 Disk Unit 0");
/*  237: 111 */     si3040.addActionListener(this);
/*  238: 112 */     si3040.setName("0");
/*  239: 113 */     this.jboot.add(si3040);
/*  240: 114 */     JMenuItem si3040_1 = new JMenuItem("SI3040 Disk Unit 1");
/*  241: 115 */     si3040_1.addActionListener(this);
/*  242: 116 */     si3040_1.setName("1");
/*  243: 117 */     this.jboot.add(si3040_1);
/*  244: 118 */     JMenuItem td8e = new JMenuItem("TD8E Tape");
/*  245: 119 */     td8e.addActionListener(this);
/*  246: 120 */     td8e.setName("2");
/*  247: 121 */     this.jboot.add(td8e);
/*  248: 122 */     JMenuItem binlo = new JMenuItem("BIN loader low speed");
/*  249: 123 */     binlo.addActionListener(this);
/*  250: 124 */     binlo.setName("3");
/*  251: 125 */     this.jboot.add(binlo);
/*  252: 126 */     JMenuItem binhi = new JMenuItem("BIN loader hi speed");
/*  253: 127 */     binhi.addActionListener(this);
/*  254: 128 */     binhi.setName("4");
/*  255: 129 */     this.jboot.add(binhi);
/*  256: 130 */     JMenuItem rimlo = new JMenuItem("RIM loader low speed");
/*  257: 131 */     rimlo.addActionListener(this);
/*  258: 132 */     rimlo.setName("5");
/*  259: 133 */     this.jboot.add(rimlo);
/*  260: 134 */     JMenuItem rimhi = new JMenuItem("RIM loader hi speed");
/*  261: 135 */     rimhi.addActionListener(this);
/*  262: 136 */     rimhi.setName("6");
/*  263: 137 */     this.jboot.add(rimhi);
/*  264: 138 */     JMenuItem binpu = new JMenuItem("BIN punch hi speed");
/*  265: 139 */     binpu.addActionListener(this);
/*  266: 140 */     binpu.setName("7");
/*  267: 141 */     this.jboot.add(binpu);
/*  268: 142 */     this.jPopupMenu1.add(this.jboot);
/*  269: 143 */     this.jPopupMenu1.addSeparator();
/*  270: 144 */     ButtonGroup group = new ButtonGroup();
/*  271: 145 */     JRadioButtonMenuItem pdpspeed = new JRadioButtonMenuItem("PDP8 Speed");
/*  272: 146 */     pdpspeed.addActionListener(this);
/*  273: 147 */     pdpspeed.setName("8");
/*  274: 148 */     group.add(pdpspeed);
/*  275: 149 */     this.jPopupMenu1.add(pdpspeed);
/*  276: 150 */     JRadioButtonMenuItem fullspeed = new JRadioButtonMenuItem("Full Speed");
/*  277: 151 */     fullspeed.addActionListener(this);
/*  278: 152 */     fullspeed.setName("9");
/*  279: 153 */     fullspeed.setSelected(true);
/*  280: 154 */     group.add(fullspeed);
/*  281: 155 */     this.jPopupMenu1.add(fullspeed);
/*  282: 156 */     this.jPopupMenu1.addSeparator();
/*  283: 157 */     ButtonGroup group2 = new ButtonGroup();
/*  284: 158 */     JRadioButtonMenuItem estyle = new JRadioButtonMenuItem("E style");
/*  285: 159 */     estyle.addActionListener(this);
/*  286: 160 */     estyle.setName("10");
/*  287: 161 */     estyle.setSelected(true);
/*  288: 162 */     group2.add(estyle);
/*  289: 163 */     this.jPopupMenu1.add(estyle);
/*  290: 164 */     JRadioButtonMenuItem istyle = new JRadioButtonMenuItem("I style");
/*  291: 165 */     istyle.addActionListener(this);
/*  292: 166 */     istyle.setName("11");
/*  293: 167 */     group2.add(istyle);
/*  294: 168 */     this.jPopupMenu1.add(istyle);
/*  295:     */   }
/*  296:     */   
/*  297:     */   private void initComponents()
/*  298:     */   {
/*  299: 182 */     this.jPopupMenu1 = new JPopupMenu();
/*  300: 183 */     this.fill1 = new JLabel();
/*  301: 184 */     this.fill2l = new JLabel();
/*  302: 185 */     this.text2Type = new JLabel();
/*  303: 186 */     this.text3Company = new JLabel();
/*  304: 187 */     this.fill2r = new JLabel();
/*  305: 188 */     this.text4MemAddr = new JLabel();
/*  306: 189 */     this.panelLampsSelector = new JPanel();
/*  307: 190 */     this.fill4l = new JLabel();
/*  308: 191 */     this.text4Ema = new JLabel();
/*  309: 192 */     this.fill4Ma0_2 = new JLabel();
/*  310: 193 */     this.fill4Ma3_4 = new JLabel();
/*  311: 194 */     this.fill4Ma6_8 = new JLabel();
/*  312: 195 */     this.fill4Ma9_11 = new JLabel();
/*  313: 196 */     this.fill4r1 = new JLabel();
/*  314: 197 */     this.text4Run = new JLabel();
/*  315: 198 */     this.fill4r2 = new JLabel();
/*  316: 199 */     this.fill5l = new JLabel();
/*  317: 200 */     this.emaLamps = new EmaLamps();
/*  318: 201 */     this.maLamps = new MaLamps();
/*  319: 202 */     this.fill5r1 = new JLabel();
/*  320: 203 */     this.runLamp = new Lamp();
/*  321: 204 */     this.fill5r2 = new JLabel();
/*  322: 205 */     this.fill6l1 = new JLabel();
/*  323: 206 */     this.fill6l2 = new JLabel();
/*  324: 207 */     this.fill6Bus0_2 = new JLabel();
/*  325: 208 */     this.fill6Bus3_5 = new JLabel();
/*  326: 209 */     this.fill6Bus6_8 = new JLabel();
/*  327: 210 */     this.fill6Bus9_11 = new JLabel();
/*  328: 211 */     this.fill6r1 = new JLabel();
/*  329: 212 */     this.fill6r2 = new JLabel();
/*  330: 213 */     this.fill6r3 = new JLabel();
/*  331: 214 */     this.panel6Selector = new JPanel();
/*  332: 215 */     this.textState = new JLabel();
/*  333: 216 */     this.textStatus = new JLabel();
/*  334: 217 */     this.textRegAc = new JLabel();
/*  335: 218 */     this.textRegMd = new JLabel();
/*  336: 219 */     this.textRegMq = new JLabel();
/*  337: 220 */     this.textRegBus = new JLabel();
/*  338: 221 */     this.knobSelect = new Knob();
/*  339: 222 */     this.stateLamps = new StateLamps();
/*  340: 223 */     this.statusLamps = new StatusLamps();
/*  341: 224 */     this.mdLamps = new MdLamps();
/*  342: 225 */     this.dataLamps = new DataLamps();
/*  343: 226 */     this.acLamps = new AcLamps();
/*  344: 227 */     this.mqLamps = new MqLamps();
/*  345: 228 */     this.fill6rbis = new JLabel();
/*  346: 229 */     this.busLamps = new BusLamps();
/*  347: 230 */     this.fill7 = new JLabel();
/*  348: 231 */     this.fill8l1 = new JLabel();
/*  349: 232 */     this.panel8Switches = new JPanel();
/*  350: 233 */     this.panelPower = new JPanel();
/*  351: 234 */     this.textOff = new JLabel();
/*  352: 235 */     this.textPower = new JLabel();
/*  353: 236 */     this.textLock = new JLabel();
/*  354: 237 */     this.knobPower = new Knob();
/*  355: 238 */     this.fill8r3 = new JLabel();
/*  356: 239 */     this.fill8l2 = new JLabel();
/*  357: 240 */     this.fill8l3 = new JLabel();
/*  358: 241 */     this.panelSwitch = new JPanel();
/*  359: 242 */     this.sw = new Switch();
/*  360: 243 */     this.textSwitch = new JLabel();
/*  361: 244 */     this.panelAlEal = new JPanel();
/*  362: 245 */     this.textAddrLoad = new JLabel();
/*  363: 246 */     this.swAl = new Switch();
/*  364: 247 */     this.textExtdAddrLoad = new JLabel();
/*  365: 248 */     this.swEal = new Switch();
/*  366: 249 */     this.fill8r1 = new JLabel();
/*  367: 250 */     this.panelStartExamHaltSS = new JPanel();
/*  368: 251 */     this.textStart = new JLabel();
/*  369: 252 */     this.textClear = new JLabel();
/*  370: 253 */     this.textCont = new JLabel();
/*  371: 254 */     this.swClear = new Switch();
/*  372: 255 */     this.swCont = new Switch();
/*  373: 256 */     this.textExam = new JLabel();
/*  374: 257 */     this.swSingStep = new Switch();
/*  375: 258 */     this.textHalt = new JLabel();
/*  376: 259 */     this.swHalt = new Switch();
/*  377: 260 */     this.textSingStep = new JLabel();
/*  378: 261 */     this.swExam = new Switch();
/*  379: 262 */     this.fill8r2 = new JLabel();
/*  380: 263 */     this.panelDep = new JPanel();
/*  381: 264 */     this.textDep = new JLabel();
/*  382: 265 */     this.swDep = new Switch();
/*  383: 266 */     this.swReg = new SwitchReg();
/*  384: 267 */     this.fill8r4 = new JLabel();
/*  385: 268 */     this.fill9 = new JLabel();
/*  386:     */     
/*  387: 270 */     setBackground(new Color(255, 255, 255));
/*  388: 271 */     setForeground(new Color(255, 51, 204));
/*  389: 272 */     setMaximumSize(new Dimension(800, 362));
/*  390: 273 */     setMinimumSize(new Dimension(400, 181));
/*  391: 274 */     setPreferredSize(new Dimension(800, 362));
/*  392: 275 */     setLayout(new GridBagLayout());
/*  393:     */     
/*  394: 277 */     this.fill1.setBackground(new Color(0, 0, 0));
/*  395: 278 */     this.fill1.setIconTextGap(40);
/*  396: 279 */     this.fill1.setMaximumSize(new Dimension(800, 40));
/*  397: 280 */     this.fill1.setMinimumSize(new Dimension(400, 20));
/*  398: 281 */     this.fill1.setOpaque(true);
/*  399: 282 */     this.fill1.setPreferredSize(new Dimension(800, 40));
/*  400: 283 */     this.fill1.addComponentListener(new ComponentAdapter()
/*  401:     */     {
/*  402:     */       public void componentResized(ComponentEvent evt)
/*  403:     */       {
/*  404: 285 */         Panel8.this.fill1ComponentResized(evt);
/*  405:     */       }
/*  406: 287 */     });
/*  407: 288 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*  408: 289 */     gridBagConstraints.gridwidth = 0;
/*  409: 290 */     add(this.fill1, gridBagConstraints);
/*  410:     */     
/*  411: 292 */     this.fill2l.setBackground(new Color(0, 0, 0));
/*  412: 293 */     this.fill2l.setIconTextGap(40);
/*  413: 294 */     this.fill2l.setMaximumSize(new Dimension(36, 46));
/*  414: 295 */     this.fill2l.setMinimumSize(new Dimension(18, 23));
/*  415: 296 */     this.fill2l.setPreferredSize(new Dimension(36, 46));
/*  416: 297 */     this.fill2l.setOpaque(true);
/*  417: 298 */     gridBagConstraints = new GridBagConstraints();
/*  418: 299 */     gridBagConstraints.gridheight = 2;
/*  419: 300 */     add(this.fill2l, gridBagConstraints);
/*  420:     */     
/*  421: 302 */     this.text2Type.setBackground(new Color(193, 77, 28));
/*  422: 303 */     this.text2Type.setFont(new Font("SansSerif", 1, 20));
/*  423: 304 */     this.text2Type.setForeground(new Color(255, 255, 255));
/*  424: 305 */     this.text2Type.setHorizontalAlignment(2);
/*  425: 306 */     this.text2Type.setText("     | d | i | g | i | t | a | l |   pdp8/e");
/*  426: 307 */     this.text2Type.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  427: 308 */     this.text2Type.setMaximumSize(new Dimension(728, 32));
/*  428: 309 */     this.text2Type.setMinimumSize(new Dimension(364, 16));
/*  429: 310 */     this.text2Type.setOpaque(true);
/*  430: 311 */     this.text2Type.setPreferredSize(new Dimension(728, 32));
/*  431: 312 */     this.text2Type.setVerticalTextPosition(1);
/*  432: 313 */     gridBagConstraints = new GridBagConstraints();
/*  433: 314 */     gridBagConstraints.gridx = 1;
/*  434: 315 */     gridBagConstraints.gridy = 1;
/*  435: 316 */     gridBagConstraints.fill = 3;
/*  436: 317 */     add(this.text2Type, gridBagConstraints);
/*  437:     */     
/*  438: 319 */     this.text3Company.setBackground(new Color(193, 132, 29));
/*  439: 320 */     this.text3Company.setFont(new Font("SansSerif", 0, 11));
/*  440: 321 */     this.text3Company.setForeground(new Color(255, 255, 255));
/*  441: 322 */     this.text3Company.setHorizontalAlignment(2);
/*  442: 323 */     this.text3Company.setText("              digital equipment corporation maynard massachusetts");
/*  443: 324 */     this.text3Company.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  444: 325 */     this.text3Company.setMaximumSize(new Dimension(728, 8));
/*  445: 326 */     this.text3Company.setMinimumSize(new Dimension(364, 4));
/*  446: 327 */     this.text3Company.setOpaque(true);
/*  447: 328 */     this.text3Company.setPreferredSize(new Dimension(728, 8));
/*  448: 329 */     gridBagConstraints = new GridBagConstraints();
/*  449: 330 */     gridBagConstraints.gridx = 1;
/*  450: 331 */     gridBagConstraints.gridy = 2;
/*  451: 332 */     gridBagConstraints.fill = 3;
/*  452: 333 */     add(this.text3Company, gridBagConstraints);
/*  453:     */     
/*  454: 335 */     this.fill2r.setBackground(new Color(0, 0, 0));
/*  455: 336 */     this.fill2r.setMaximumSize(new Dimension(36, 46));
/*  456: 337 */     this.fill2r.setMinimumSize(new Dimension(18, 23));
/*  457: 338 */     this.fill2r.setPreferredSize(new Dimension(36, 46));
/*  458: 339 */     this.fill2r.setOpaque(true);
/*  459: 340 */     gridBagConstraints = new GridBagConstraints();
/*  460: 341 */     gridBagConstraints.gridx = 2;
/*  461: 342 */     gridBagConstraints.gridwidth = 0;
/*  462: 343 */     gridBagConstraints.gridheight = 2;
/*  463: 344 */     add(this.fill2r, gridBagConstraints);
/*  464:     */     
/*  465: 346 */     this.text4MemAddr.setBackground(new Color(0, 0, 0));
/*  466: 347 */     this.text4MemAddr.setFont(new Font("SansSerif", 0, 9));
/*  467: 348 */     this.text4MemAddr.setForeground(new Color(255, 255, 255));
/*  468: 349 */     this.text4MemAddr.setHorizontalAlignment(0);
/*  469: 350 */     this.text4MemAddr.setText("MEMORY ADDRESS");
/*  470: 351 */     this.text4MemAddr.setVerticalAlignment(3);
/*  471: 352 */     this.text4MemAddr.setMaximumSize(new Dimension(800, 20));
/*  472: 353 */     this.text4MemAddr.setMinimumSize(new Dimension(400, 10));
/*  473: 354 */     this.text4MemAddr.setOpaque(true);
/*  474: 355 */     this.text4MemAddr.setPreferredSize(new Dimension(800, 20));
/*  475: 356 */     gridBagConstraints = new GridBagConstraints();
/*  476: 357 */     gridBagConstraints.gridx = 0;
/*  477: 358 */     gridBagConstraints.gridy = 3;
/*  478: 359 */     gridBagConstraints.gridwidth = 0;
/*  479: 360 */     add(this.text4MemAddr, gridBagConstraints);
/*  480:     */     
/*  481: 362 */     this.panelLampsSelector.setMaximumSize(new Dimension(800, 130));
/*  482: 363 */     this.panelLampsSelector.setLayout(new GridBagLayout());
/*  483:     */     
/*  484: 365 */     this.fill4l.setBackground(new Color(0, 0, 0));
/*  485: 366 */     this.fill4l.setMaximumSize(new Dimension(110, 10));
/*  486: 367 */     this.fill4l.setMinimumSize(new Dimension(55, 5));
/*  487: 368 */     this.fill4l.setOpaque(true);
/*  488: 369 */     this.fill4l.setPreferredSize(new Dimension(110, 10));
/*  489: 370 */     gridBagConstraints = new GridBagConstraints();
/*  490: 371 */     gridBagConstraints.anchor = 17;
/*  491: 372 */     this.panelLampsSelector.add(this.fill4l, gridBagConstraints);
/*  492:     */     
/*  493: 374 */     this.text4Ema.setBackground(new Color(193, 132, 29));
/*  494: 375 */     this.text4Ema.setFont(new Font("Dialog", 0, 8));
/*  495: 376 */     this.text4Ema.setForeground(new Color(255, 255, 255));
/*  496: 377 */     this.text4Ema.setHorizontalAlignment(0);
/*  497: 378 */     this.text4Ema.setText("EMA");
/*  498: 379 */     this.text4Ema.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  499: 380 */     this.text4Ema.setMaximumSize(new Dimension(78, 10));
/*  500: 381 */     this.text4Ema.setMinimumSize(new Dimension(39, 5));
/*  501: 382 */     this.text4Ema.setOpaque(true);
/*  502: 383 */     this.text4Ema.setPreferredSize(new Dimension(78, 10));
/*  503: 384 */     this.panelLampsSelector.add(this.text4Ema, new GridBagConstraints());
/*  504:     */     
/*  505: 386 */     this.fill4Ma0_2.setBackground(new Color(193, 77, 28));
/*  506: 387 */     this.fill4Ma0_2.setFont(new Font("Dialog", 1, 10));
/*  507: 388 */     this.fill4Ma0_2.setHorizontalAlignment(0);
/*  508: 389 */     this.fill4Ma0_2.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  509: 390 */     this.fill4Ma0_2.setMaximumSize(new Dimension(78, 10));
/*  510: 391 */     this.fill4Ma0_2.setMinimumSize(new Dimension(39, 5));
/*  511: 392 */     this.fill4Ma0_2.setOpaque(true);
/*  512: 393 */     this.fill4Ma0_2.setPreferredSize(new Dimension(78, 10));
/*  513: 394 */     this.panelLampsSelector.add(this.fill4Ma0_2, new GridBagConstraints());
/*  514:     */     
/*  515: 396 */     this.fill4Ma3_4.setBackground(new Color(193, 132, 29));
/*  516: 397 */     this.fill4Ma3_4.setFont(new Font("Dialog", 1, 10));
/*  517: 398 */     this.fill4Ma3_4.setHorizontalAlignment(0);
/*  518: 399 */     this.fill4Ma3_4.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  519: 400 */     this.fill4Ma3_4.setMaximumSize(new Dimension(78, 10));
/*  520: 401 */     this.fill4Ma3_4.setMinimumSize(new Dimension(39, 5));
/*  521: 402 */     this.fill4Ma3_4.setOpaque(true);
/*  522: 403 */     this.fill4Ma3_4.setPreferredSize(new Dimension(78, 10));
/*  523: 404 */     this.panelLampsSelector.add(this.fill4Ma3_4, new GridBagConstraints());
/*  524:     */     
/*  525: 406 */     this.fill4Ma6_8.setBackground(new Color(193, 77, 28));
/*  526: 407 */     this.fill4Ma6_8.setFont(new Font("Dialog", 1, 10));
/*  527: 408 */     this.fill4Ma6_8.setHorizontalAlignment(0);
/*  528: 409 */     this.fill4Ma6_8.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  529: 410 */     this.fill4Ma6_8.setMaximumSize(new Dimension(78, 10));
/*  530: 411 */     this.fill4Ma6_8.setMinimumSize(new Dimension(39, 5));
/*  531: 412 */     this.fill4Ma6_8.setPreferredSize(new Dimension(78, 10));
/*  532: 413 */     this.fill4Ma6_8.setOpaque(true);
/*  533: 414 */     this.panelLampsSelector.add(this.fill4Ma6_8, new GridBagConstraints());
/*  534:     */     
/*  535: 416 */     this.fill4Ma9_11.setBackground(new Color(193, 132, 29));
/*  536: 417 */     this.fill4Ma9_11.setFont(new Font("Dialog", 1, 10));
/*  537: 418 */     this.fill4Ma9_11.setHorizontalAlignment(0);
/*  538: 419 */     this.fill4Ma9_11.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  539: 420 */     this.fill4Ma9_11.setMaximumSize(new Dimension(78, 10));
/*  540: 421 */     this.fill4Ma9_11.setMinimumSize(new Dimension(39, 5));
/*  541: 422 */     this.fill4Ma9_11.setOpaque(true);
/*  542: 423 */     this.fill4Ma9_11.setPreferredSize(new Dimension(78, 10));
/*  543: 424 */     this.panelLampsSelector.add(this.fill4Ma9_11, new GridBagConstraints());
/*  544:     */     
/*  545: 426 */     this.fill4r1.setBackground(new Color(0, 0, 0));
/*  546: 427 */     this.fill4r1.setMaximumSize(new Dimension(70, 10));
/*  547: 428 */     this.fill4r1.setMinimumSize(new Dimension(35, 5));
/*  548: 429 */     this.fill4r1.setOpaque(true);
/*  549: 430 */     this.fill4r1.setPreferredSize(new Dimension(70, 10));
/*  550: 431 */     this.panelLampsSelector.add(this.fill4r1, new GridBagConstraints());
/*  551:     */     
/*  552: 433 */     this.text4Run.setBackground(new Color(193, 132, 29));
/*  553: 434 */     this.text4Run.setFont(new Font("Dialog", 0, 8));
/*  554: 435 */     this.text4Run.setForeground(new Color(255, 255, 255));
/*  555: 436 */     this.text4Run.setHorizontalAlignment(0);
/*  556: 437 */     this.text4Run.setText("RUN");
/*  557: 438 */     this.text4Run.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  558: 439 */     this.text4Run.setMaximumSize(new Dimension(26, 10));
/*  559: 440 */     this.text4Run.setMinimumSize(new Dimension(13, 5));
/*  560: 441 */     this.text4Run.setOpaque(true);
/*  561: 442 */     this.text4Run.setPreferredSize(new Dimension(26, 10));
/*  562: 443 */     this.panelLampsSelector.add(this.text4Run, new GridBagConstraints());
/*  563:     */     
/*  564: 445 */     this.fill4r2.setBackground(new Color(0, 0, 0));
/*  565: 446 */     this.fill4r2.setMaximumSize(new Dimension(204, 10));
/*  566: 447 */     this.fill4r2.setMinimumSize(new Dimension(102, 5));
/*  567: 448 */     this.fill4r2.setOpaque(true);
/*  568: 449 */     this.fill4r2.setPreferredSize(new Dimension(204, 10));
/*  569: 450 */     gridBagConstraints = new GridBagConstraints();
/*  570: 451 */     gridBagConstraints.gridwidth = 0;
/*  571: 452 */     gridBagConstraints.fill = 2;
/*  572: 453 */     this.panelLampsSelector.add(this.fill4r2, gridBagConstraints);
/*  573:     */     
/*  574: 455 */     this.fill5l.setBackground(new Color(0, 0, 0));
/*  575: 456 */     this.fill5l.setMaximumSize(new Dimension(110, 10));
/*  576: 457 */     this.fill5l.setMinimumSize(new Dimension(55, 5));
/*  577: 458 */     this.fill5l.setOpaque(true);
/*  578: 459 */     this.fill5l.setPreferredSize(new Dimension(110, 10));
/*  579: 460 */     this.panelLampsSelector.add(this.fill5l, new GridBagConstraints());
/*  580:     */     
/*  581: 462 */     this.emaLamps.setName("ema");
/*  582: 463 */     this.panelLampsSelector.add(this.emaLamps, new GridBagConstraints());
/*  583:     */     
/*  584: 465 */     this.maLamps.setName("ma");
/*  585: 466 */     gridBagConstraints = new GridBagConstraints();
/*  586: 467 */     gridBagConstraints.gridwidth = 4;
/*  587: 468 */     this.panelLampsSelector.add(this.maLamps, gridBagConstraints);
/*  588:     */     
/*  589: 470 */     this.fill5r1.setBackground(new Color(0, 0, 0));
/*  590: 471 */     this.fill5r1.setMaximumSize(new Dimension(70, 10));
/*  591: 472 */     this.fill5r1.setMinimumSize(new Dimension(35, 5));
/*  592: 473 */     this.fill5r1.setOpaque(true);
/*  593: 474 */     this.fill5r1.setPreferredSize(new Dimension(70, 10));
/*  594: 475 */     this.panelLampsSelector.add(this.fill5r1, new GridBagConstraints());
/*  595:     */     
/*  596: 477 */     this.runLamp.setMaximumSize(new Dimension(26, 10));
/*  597: 478 */     this.runLamp.setMinimumSize(new Dimension(13, 5));
/*  598: 479 */     this.runLamp.setName("run");
/*  599: 480 */     this.runLamp.setPreferredSize(new Dimension(26, 10));
/*  600: 481 */     this.panelLampsSelector.add(this.runLamp, new GridBagConstraints());
/*  601:     */     
/*  602: 483 */     this.fill5r2.setBackground(new Color(0, 0, 0));
/*  603: 484 */     this.fill5r2.setMaximumSize(new Dimension(204, 10));
/*  604: 485 */     this.fill5r2.setMinimumSize(new Dimension(102, 5));
/*  605: 486 */     this.fill5r2.setOpaque(true);
/*  606: 487 */     this.fill5r2.setPreferredSize(new Dimension(204, 10));
/*  607: 488 */     gridBagConstraints = new GridBagConstraints();
/*  608: 489 */     gridBagConstraints.gridwidth = 0;
/*  609: 490 */     gridBagConstraints.fill = 2;
/*  610: 491 */     this.panelLampsSelector.add(this.fill5r2, gridBagConstraints);
/*  611:     */     
/*  612: 493 */     this.fill6l1.setBackground(new Color(0, 0, 0));
/*  613: 494 */     this.fill6l1.setMaximumSize(new Dimension(110, 108));
/*  614: 495 */     this.fill6l1.setMinimumSize(new Dimension(55, 54));
/*  615: 496 */     this.fill6l1.setOpaque(true);
/*  616: 497 */     this.fill6l1.setPreferredSize(new Dimension(110, 108));
/*  617: 498 */     gridBagConstraints = new GridBagConstraints();
/*  618: 499 */     gridBagConstraints.gridheight = 3;
/*  619: 500 */     this.panelLampsSelector.add(this.fill6l1, gridBagConstraints);
/*  620:     */     
/*  621: 502 */     this.fill6l2.setBackground(new Color(0, 0, 0));
/*  622: 503 */     this.fill6l2.setMaximumSize(new Dimension(78, 108));
/*  623: 504 */     this.fill6l2.setMinimumSize(new Dimension(39, 54));
/*  624: 505 */     this.fill6l2.setOpaque(true);
/*  625: 506 */     this.fill6l2.setPreferredSize(new Dimension(78, 108));
/*  626: 507 */     gridBagConstraints = new GridBagConstraints();
/*  627: 508 */     gridBagConstraints.gridheight = 3;
/*  628: 509 */     gridBagConstraints.anchor = 11;
/*  629: 510 */     this.panelLampsSelector.add(this.fill6l2, gridBagConstraints);
/*  630:     */     
/*  631: 512 */     this.fill6Bus0_2.setBackground(new Color(0, 0, 0));
/*  632: 513 */     this.fill6Bus0_2.setMaximumSize(new Dimension(78, 22));
/*  633: 514 */     this.fill6Bus0_2.setMinimumSize(new Dimension(39, 11));
/*  634: 515 */     this.fill6Bus0_2.setOpaque(true);
/*  635: 516 */     this.fill6Bus0_2.setPreferredSize(new Dimension(78, 22));
/*  636: 517 */     gridBagConstraints = new GridBagConstraints();
/*  637: 518 */     gridBagConstraints.anchor = 11;
/*  638: 519 */     this.panelLampsSelector.add(this.fill6Bus0_2, gridBagConstraints);
/*  639:     */     
/*  640: 521 */     this.fill6Bus3_5.setBackground(new Color(0, 0, 0));
/*  641: 522 */     this.fill6Bus3_5.setMaximumSize(new Dimension(78, 22));
/*  642: 523 */     this.fill6Bus3_5.setMinimumSize(new Dimension(39, 11));
/*  643: 524 */     this.fill6Bus3_5.setOpaque(true);
/*  644: 525 */     this.fill6Bus3_5.setPreferredSize(new Dimension(78, 22));
/*  645: 526 */     gridBagConstraints = new GridBagConstraints();
/*  646: 527 */     gridBagConstraints.anchor = 11;
/*  647: 528 */     this.panelLampsSelector.add(this.fill6Bus3_5, gridBagConstraints);
/*  648:     */     
/*  649: 530 */     this.fill6Bus6_8.setBackground(new Color(0, 0, 0));
/*  650: 531 */     this.fill6Bus6_8.setMaximumSize(new Dimension(78, 22));
/*  651: 532 */     this.fill6Bus6_8.setMinimumSize(new Dimension(39, 11));
/*  652: 533 */     this.fill6Bus6_8.setOpaque(true);
/*  653: 534 */     this.fill6Bus6_8.setPreferredSize(new Dimension(78, 22));
/*  654: 535 */     gridBagConstraints = new GridBagConstraints();
/*  655: 536 */     gridBagConstraints.anchor = 11;
/*  656: 537 */     this.panelLampsSelector.add(this.fill6Bus6_8, gridBagConstraints);
/*  657:     */     
/*  658: 539 */     this.fill6Bus9_11.setBackground(new Color(0, 0, 0));
/*  659: 540 */     this.fill6Bus9_11.setMaximumSize(new Dimension(78, 22));
/*  660: 541 */     this.fill6Bus9_11.setMinimumSize(new Dimension(39, 11));
/*  661: 542 */     this.fill6Bus9_11.setOpaque(true);
/*  662: 543 */     this.fill6Bus9_11.setPreferredSize(new Dimension(78, 22));
/*  663: 544 */     gridBagConstraints = new GridBagConstraints();
/*  664: 545 */     gridBagConstraints.anchor = 11;
/*  665: 546 */     this.panelLampsSelector.add(this.fill6Bus9_11, gridBagConstraints);
/*  666:     */     
/*  667: 548 */     this.fill6r1.setBackground(new Color(0, 0, 0));
/*  668: 549 */     this.fill6r1.setMaximumSize(new Dimension(70, 32));
/*  669: 550 */     this.fill6r1.setMinimumSize(new Dimension(35, 16));
/*  670: 551 */     this.fill6r1.setOpaque(true);
/*  671: 552 */     this.fill6r1.setPreferredSize(new Dimension(70, 32));
/*  672: 553 */     gridBagConstraints = new GridBagConstraints();
/*  673: 554 */     gridBagConstraints.gridheight = 2;
/*  674: 555 */     this.panelLampsSelector.add(this.fill6r1, gridBagConstraints);
/*  675:     */     
/*  676: 557 */     this.fill6r2.setBackground(new Color(0, 0, 0));
/*  677: 558 */     this.fill6r2.setMaximumSize(new Dimension(26, 32));
/*  678: 559 */     this.fill6r2.setMinimumSize(new Dimension(13, 16));
/*  679: 560 */     this.fill6r2.setOpaque(true);
/*  680: 561 */     this.fill6r2.setPreferredSize(new Dimension(26, 32));
/*  681: 562 */     gridBagConstraints = new GridBagConstraints();
/*  682: 563 */     gridBagConstraints.gridheight = 2;
/*  683: 564 */     this.panelLampsSelector.add(this.fill6r2, gridBagConstraints);
/*  684:     */     
/*  685: 566 */     this.fill6r3.setBackground(new Color(0, 0, 0));
/*  686: 567 */     this.fill6r3.setMaximumSize(new Dimension(204, 32));
/*  687: 568 */     this.fill6r3.setMinimumSize(new Dimension(102, 16));
/*  688: 569 */     this.fill6r3.setOpaque(true);
/*  689: 570 */     this.fill6r3.setPreferredSize(new Dimension(204, 32));
/*  690: 571 */     gridBagConstraints = new GridBagConstraints();
/*  691: 572 */     gridBagConstraints.gridwidth = 0;
/*  692: 573 */     gridBagConstraints.gridheight = 2;
/*  693: 574 */     this.panelLampsSelector.add(this.fill6r3, gridBagConstraints);
/*  694:     */     
/*  695: 576 */     this.panel6Selector.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  696: 577 */     this.panel6Selector.setMaximumSize(new Dimension(408, 76));
/*  697: 578 */     this.panel6Selector.setMinimumSize(new Dimension(204, 38));
/*  698: 579 */     this.panel6Selector.setPreferredSize(new Dimension(408, 76));
/*  699: 580 */     this.panel6Selector.setLayout(new GridBagLayout());
/*  700:     */     
/*  701: 582 */     this.textState.setBackground(new Color(0, 0, 0));
/*  702: 583 */     this.textState.setFont(new Font("Dialog", 0, 7));
/*  703: 584 */     this.textState.setForeground(new Color(255, 255, 255));
/*  704: 585 */     this.textState.setText(" STATE ");
/*  705: 586 */     this.textState.setMaximumSize(new Dimension(40, 12));
/*  706: 587 */     this.textState.setMinimumSize(new Dimension(20, 6));
/*  707: 588 */     this.textState.setOpaque(true);
/*  708: 589 */     this.textState.setPreferredSize(new Dimension(40, 12));
/*  709: 590 */     gridBagConstraints = new GridBagConstraints();
/*  710: 591 */     gridBagConstraints.gridx = 1;
/*  711: 592 */     gridBagConstraints.gridy = 0;
/*  712: 593 */     this.panel6Selector.add(this.textState, gridBagConstraints);
/*  713:     */     
/*  714: 595 */     this.textStatus.setBackground(new Color(0, 0, 0));
/*  715: 596 */     this.textStatus.setFont(new Font("Dialog", 0, 7));
/*  716: 597 */     this.textStatus.setForeground(new Color(255, 255, 255));
/*  717: 598 */     this.textStatus.setText(" STATUS");
/*  718: 599 */     this.textStatus.setMaximumSize(new Dimension(40, 12));
/*  719: 600 */     this.textStatus.setMinimumSize(new Dimension(20, 6));
/*  720: 601 */     this.textStatus.setOpaque(true);
/*  721: 602 */     this.textStatus.setPreferredSize(new Dimension(40, 12));
/*  722: 603 */     gridBagConstraints = new GridBagConstraints();
/*  723: 604 */     gridBagConstraints.gridx = 1;
/*  724: 605 */     gridBagConstraints.gridy = 1;
/*  725: 606 */     this.panel6Selector.add(this.textStatus, gridBagConstraints);
/*  726:     */     
/*  727: 608 */     this.textRegAc.setBackground(new Color(0, 0, 0));
/*  728: 609 */     this.textRegAc.setFont(new Font("Dialog", 0, 7));
/*  729: 610 */     this.textRegAc.setForeground(new Color(255, 255, 255));
/*  730: 611 */     this.textRegAc.setText(" AC ");
/*  731: 612 */     this.textRegAc.setMaximumSize(new Dimension(40, 12));
/*  732: 613 */     this.textRegAc.setMinimumSize(new Dimension(20, 6));
/*  733: 614 */     this.textRegAc.setOpaque(true);
/*  734: 615 */     this.textRegAc.setPreferredSize(new Dimension(40, 12));
/*  735: 616 */     gridBagConstraints = new GridBagConstraints();
/*  736: 617 */     gridBagConstraints.gridx = 1;
/*  737: 618 */     gridBagConstraints.gridy = 2;
/*  738: 619 */     this.panel6Selector.add(this.textRegAc, gridBagConstraints);
/*  739:     */     
/*  740: 621 */     this.textRegMd.setBackground(new Color(0, 0, 0));
/*  741: 622 */     this.textRegMd.setFont(new Font("Dialog", 0, 7));
/*  742: 623 */     this.textRegMd.setForeground(new Color(255, 255, 255));
/*  743: 624 */     this.textRegMd.setText(" MD ");
/*  744: 625 */     this.textRegMd.setMaximumSize(new Dimension(40, 12));
/*  745: 626 */     this.textRegMd.setMinimumSize(new Dimension(20, 6));
/*  746: 627 */     this.textRegMd.setOpaque(true);
/*  747: 628 */     this.textRegMd.setPreferredSize(new Dimension(40, 12));
/*  748: 629 */     gridBagConstraints = new GridBagConstraints();
/*  749: 630 */     gridBagConstraints.gridx = 1;
/*  750: 631 */     gridBagConstraints.gridy = 3;
/*  751: 632 */     this.panel6Selector.add(this.textRegMd, gridBagConstraints);
/*  752:     */     
/*  753: 634 */     this.textRegMq.setBackground(new Color(0, 0, 0));
/*  754: 635 */     this.textRegMq.setFont(new Font("Dialog", 0, 7));
/*  755: 636 */     this.textRegMq.setForeground(new Color(255, 255, 255));
/*  756: 637 */     this.textRegMq.setText(" MQ  ");
/*  757: 638 */     this.textRegMq.setMaximumSize(new Dimension(40, 12));
/*  758: 639 */     this.textRegMq.setMinimumSize(new Dimension(20, 6));
/*  759: 640 */     this.textRegMq.setOpaque(true);
/*  760: 641 */     this.textRegMq.setPreferredSize(new Dimension(40, 12));
/*  761: 642 */     gridBagConstraints = new GridBagConstraints();
/*  762: 643 */     gridBagConstraints.gridx = 1;
/*  763: 644 */     gridBagConstraints.gridy = 4;
/*  764: 645 */     this.panel6Selector.add(this.textRegMq, gridBagConstraints);
/*  765:     */     
/*  766: 647 */     this.textRegBus.setBackground(new Color(0, 0, 0));
/*  767: 648 */     this.textRegBus.setFont(new Font("Dialog", 0, 7));
/*  768: 649 */     this.textRegBus.setForeground(new Color(255, 255, 255));
/*  769: 650 */     this.textRegBus.setText(" BUS  ");
/*  770: 651 */     this.textRegBus.setMaximumSize(new Dimension(40, 12));
/*  771: 652 */     this.textRegBus.setMinimumSize(new Dimension(20, 6));
/*  772: 653 */     this.textRegBus.setOpaque(true);
/*  773: 654 */     this.textRegBus.setPreferredSize(new Dimension(40, 12));
/*  774: 655 */     gridBagConstraints = new GridBagConstraints();
/*  775: 656 */     gridBagConstraints.gridx = 1;
/*  776: 657 */     gridBagConstraints.gridy = 5;
/*  777: 658 */     this.panel6Selector.add(this.textRegBus, gridBagConstraints);
/*  778:     */     
/*  779: 660 */     this.knobSelect.setBackground(new Color(0, 0, 0));
/*  780: 661 */     this.knobSelect.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 2));
/*  781: 662 */     this.knobSelect.setLength(110.0F);
/*  782: 663 */     this.knobSelect.setMaximumSize(new Dimension(52, 72));
/*  783: 664 */     this.knobSelect.setMinimumSize(new Dimension(26, 36));
/*  784: 665 */     this.knobSelect.setName("select");
/*  785: 666 */     this.knobSelect.setPreferredSize(new Dimension(52, 72));
/*  786: 667 */     this.knobSelect.setStart(235.0F);
/*  787: 668 */     gridBagConstraints = new GridBagConstraints();
/*  788: 669 */     gridBagConstraints.gridheight = 6;
/*  789: 670 */     this.panel6Selector.add(this.knobSelect, gridBagConstraints);
/*  790: 671 */     gridBagConstraints = new GridBagConstraints();
/*  791: 672 */     gridBagConstraints.gridx = 0;
/*  792: 673 */     gridBagConstraints.gridy = 0;
/*  793: 674 */     this.panel6Selector.add(this.stateLamps, gridBagConstraints);
/*  794: 675 */     gridBagConstraints = new GridBagConstraints();
/*  795: 676 */     gridBagConstraints.gridx = 0;
/*  796: 677 */     gridBagConstraints.gridy = 1;
/*  797: 678 */     this.panel6Selector.add(this.statusLamps, gridBagConstraints);
/*  798: 679 */     gridBagConstraints = new GridBagConstraints();
/*  799: 680 */     gridBagConstraints.gridx = 0;
/*  800: 681 */     gridBagConstraints.gridy = 3;
/*  801: 682 */     this.panel6Selector.add(this.mdLamps, gridBagConstraints);
/*  802: 683 */     gridBagConstraints = new GridBagConstraints();
/*  803: 684 */     gridBagConstraints.gridx = 0;
/*  804: 685 */     gridBagConstraints.gridy = 5;
/*  805: 686 */     this.panel6Selector.add(this.dataLamps, gridBagConstraints);
/*  806: 687 */     gridBagConstraints = new GridBagConstraints();
/*  807: 688 */     gridBagConstraints.gridx = 0;
/*  808: 689 */     gridBagConstraints.gridy = 2;
/*  809: 690 */     this.panel6Selector.add(this.acLamps, gridBagConstraints);
/*  810: 691 */     gridBagConstraints = new GridBagConstraints();
/*  811: 692 */     gridBagConstraints.gridx = 0;
/*  812: 693 */     gridBagConstraints.gridy = 4;
/*  813: 694 */     this.panel6Selector.add(this.mqLamps, gridBagConstraints);
/*  814:     */     
/*  815: 696 */     gridBagConstraints = new GridBagConstraints();
/*  816: 697 */     gridBagConstraints.gridx = 2;
/*  817: 698 */     gridBagConstraints.gridy = 4;
/*  818: 699 */     gridBagConstraints.gridwidth = 6;
/*  819: 700 */     gridBagConstraints.anchor = 17;
/*  820: 701 */     this.panelLampsSelector.add(this.panel6Selector, gridBagConstraints);
/*  821:     */     
/*  822: 703 */     this.fill6rbis.setBackground(new Color(0, 0, 0));
/*  823: 704 */     this.fill6rbis.setMaximumSize(new Dimension(204, 76));
/*  824: 705 */     this.fill6rbis.setMinimumSize(new Dimension(102, 38));
/*  825: 706 */     this.fill6rbis.setOpaque(true);
/*  826: 707 */     this.fill6rbis.setPreferredSize(new Dimension(204, 76));
/*  827: 708 */     gridBagConstraints = new GridBagConstraints();
/*  828: 709 */     gridBagConstraints.gridwidth = 0;
/*  829: 710 */     gridBagConstraints.gridheight = 2;
/*  830: 711 */     this.panelLampsSelector.add(this.fill6rbis, gridBagConstraints);
/*  831:     */     
/*  832: 713 */     this.busLamps.setName("bus");
/*  833: 714 */     gridBagConstraints = new GridBagConstraints();
/*  834: 715 */     gridBagConstraints.gridx = 2;
/*  835: 716 */     gridBagConstraints.gridy = 3;
/*  836: 717 */     gridBagConstraints.gridwidth = 4;
/*  837: 718 */     this.panelLampsSelector.add(this.busLamps, gridBagConstraints);
/*  838:     */     
/*  839: 720 */     gridBagConstraints = new GridBagConstraints();
/*  840: 721 */     gridBagConstraints.gridwidth = 0;
/*  841: 722 */     gridBagConstraints.fill = 2;
/*  842: 723 */     add(this.panelLampsSelector, gridBagConstraints);
/*  843:     */     
/*  844: 725 */     this.fill7.setBackground(new Color(0, 0, 0));
/*  845: 726 */     this.fill7.setIconTextGap(40);
/*  846: 727 */     this.fill7.setMaximumSize(new Dimension(800, 24));
/*  847: 728 */     this.fill7.setMinimumSize(new Dimension(400, 12));
/*  848: 729 */     this.fill7.setOpaque(true);
/*  849: 730 */     this.fill7.setPreferredSize(new Dimension(800, 24));
/*  850: 731 */     gridBagConstraints = new GridBagConstraints();
/*  851: 732 */     gridBagConstraints.gridwidth = 0;
/*  852: 733 */     add(this.fill7, gridBagConstraints);
/*  853:     */     
/*  854: 735 */     this.fill8l1.setBackground(new Color(0, 0, 0));
/*  855: 736 */     this.fill8l1.setBorder(BorderFactory.createBevelBorder(0));
/*  856: 737 */     this.fill8l1.setMaximumSize(new Dimension(36, 72));
/*  857: 738 */     this.fill8l1.setMinimumSize(new Dimension(18, 36));
/*  858: 739 */     this.fill8l1.setOpaque(true);
/*  859: 740 */     this.fill8l1.setPreferredSize(new Dimension(36, 72));
/*  860: 741 */     gridBagConstraints = new GridBagConstraints();
/*  861: 742 */     gridBagConstraints.anchor = 17;
/*  862: 743 */     add(this.fill8l1, gridBagConstraints);
/*  863:     */     
/*  864: 745 */     this.panel8Switches.setBackground(new Color(153, 153, 153));
/*  865: 746 */     this.panel8Switches.setMaximumSize(new Dimension(728, 72));
/*  866: 747 */     this.panel8Switches.setMinimumSize(new Dimension(364, 36));
/*  867: 748 */     this.panel8Switches.setPreferredSize(new Dimension(728, 72));
/*  868: 749 */     this.panel8Switches.setLayout(new GridBagLayout());
/*  869:     */     
/*  870: 751 */     this.panelPower.setBackground(new Color(0, 0, 0));
/*  871: 752 */     this.panelPower.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)), BorderFactory.createLineBorder(new Color(255, 255, 255)))));
/*  872: 753 */     this.panelPower.setMaximumSize(new Dimension(84, 72));
/*  873: 754 */     this.panelPower.setMinimumSize(new Dimension(42, 36));
/*  874: 755 */     this.panelPower.setLayout(new GridBagLayout());
/*  875:     */     
/*  876: 757 */     this.textOff.setBackground(new Color(193, 77, 28));
/*  877: 758 */     this.textOff.setFont(new Font("Dialog", 0, 8));
/*  878: 759 */     this.textOff.setForeground(new Color(255, 255, 255));
/*  879: 760 */     this.textOff.setHorizontalAlignment(0);
/*  880: 761 */     this.textOff.setText("OFF");
/*  881: 762 */     this.textOff.setVerticalAlignment(1);
/*  882: 763 */     this.textOff.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  883: 764 */     this.textOff.setMaximumSize(new Dimension(26, 66));
/*  884: 765 */     this.textOff.setMinimumSize(new Dimension(13, 33));
/*  885: 766 */     this.textOff.setOpaque(true);
/*  886: 767 */     this.textOff.setPreferredSize(new Dimension(26, 66));
/*  887: 768 */     gridBagConstraints = new GridBagConstraints();
/*  888: 769 */     gridBagConstraints.gridheight = 2;
/*  889: 770 */     this.panelPower.add(this.textOff, gridBagConstraints);
/*  890:     */     
/*  891: 772 */     this.textPower.setBackground(new Color(193, 132, 28));
/*  892: 773 */     this.textPower.setFont(new Font("Dialog", 0, 8));
/*  893: 774 */     this.textPower.setForeground(new Color(255, 255, 255));
/*  894: 775 */     this.textPower.setHorizontalAlignment(0);
/*  895: 776 */     this.textPower.setText("PW");
/*  896: 777 */     this.textPower.setToolTipText("POWER");
/*  897: 778 */     this.textPower.setVerticalAlignment(1);
/*  898: 779 */     this.textPower.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  899: 780 */     this.textPower.setMaximumSize(new Dimension(26, 22));
/*  900: 781 */     this.textPower.setMinimumSize(new Dimension(13, 11));
/*  901: 782 */     this.textPower.setOpaque(true);
/*  902: 783 */     this.textPower.setPreferredSize(new Dimension(26, 22));
/*  903: 784 */     this.panelPower.add(this.textPower, new GridBagConstraints());
/*  904:     */     
/*  905: 786 */     this.textLock.setBackground(new Color(193, 77, 28));
/*  906: 787 */     this.textLock.setFont(new Font("Dialog", 0, 8));
/*  907: 788 */     this.textLock.setForeground(new Color(255, 255, 255));
/*  908: 789 */     this.textLock.setHorizontalAlignment(0);
/*  909: 790 */     this.textLock.setText("LOCK");
/*  910: 791 */     this.textLock.setVerticalAlignment(1);
/*  911: 792 */     this.textLock.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  912: 793 */     this.textLock.setMaximumSize(new Dimension(26, 66));
/*  913: 794 */     this.textLock.setMinimumSize(new Dimension(13, 33));
/*  914: 795 */     this.textLock.setOpaque(true);
/*  915: 796 */     this.textLock.setPreferredSize(new Dimension(26, 66));
/*  916: 797 */     gridBagConstraints = new GridBagConstraints();
/*  917: 798 */     gridBagConstraints.gridheight = 2;
/*  918: 799 */     this.panelPower.add(this.textLock, gridBagConstraints);
/*  919:     */     
/*  920: 801 */     this.knobPower.setBackground(new Color(193, 132, 29));
/*  921: 802 */     this.knobPower.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/*  922: 803 */     this.knobPower.setLength(100.0F);
/*  923: 804 */     this.knobPower.setMaximumSize(new Dimension(26, 44));
/*  924: 805 */     this.knobPower.setMinimumSize(new Dimension(13, 22));
/*  925: 806 */     this.knobPower.setName("power");
/*  926: 807 */     this.knobPower.setPreferredSize(new Dimension(26, 44));
/*  927: 808 */     this.knobPower.setStart(140.0F);
/*  928: 809 */     this.knobPower.setTicks(3);
/*  929: 810 */     this.knobPower.setValue(0.5F);
/*  930: 811 */     gridBagConstraints = new GridBagConstraints();
/*  931: 812 */     gridBagConstraints.gridx = 1;
/*  932: 813 */     gridBagConstraints.gridy = 1;
/*  933: 814 */     this.panelPower.add(this.knobPower, gridBagConstraints);
/*  934:     */     
/*  935: 816 */     gridBagConstraints = new GridBagConstraints();
/*  936: 817 */     gridBagConstraints.gridx = 0;
/*  937: 818 */     gridBagConstraints.gridy = 0;
/*  938: 819 */     this.panel8Switches.add(this.panelPower, gridBagConstraints);
/*  939:     */     
/*  940: 821 */     this.fill8r3.setBackground(new Color(0, 0, 0));
/*  941: 822 */     this.fill8r3.setFont(new Font("Dialog", 0, 7));
/*  942: 823 */     this.fill8r3.setForeground(new Color(255, 255, 255));
/*  943: 824 */     this.fill8r3.setHorizontalAlignment(0);
/*  944: 825 */     this.fill8r3.setVerticalAlignment(1);
/*  945: 826 */     this.fill8r3.setMaximumSize(new Dimension(16, 72));
/*  946: 827 */     this.fill8r3.setMinimumSize(new Dimension(6, 36));
/*  947: 828 */     this.fill8r3.setOpaque(true);
/*  948: 829 */     this.fill8r3.setPreferredSize(new Dimension(16, 72));
/*  949: 830 */     gridBagConstraints = new GridBagConstraints();
/*  950: 831 */     gridBagConstraints.gridx = 9;
/*  951: 832 */     gridBagConstraints.gridy = 0;
/*  952: 833 */     this.panel8Switches.add(this.fill8r3, gridBagConstraints);
/*  953:     */     
/*  954: 835 */     this.fill8l2.setBackground(new Color(0, 0, 0));
/*  955: 836 */     this.fill8l2.setFont(new Font("Dialog", 0, 7));
/*  956: 837 */     this.fill8l2.setForeground(new Color(255, 255, 255));
/*  957: 838 */     this.fill8l2.setHorizontalAlignment(0);
/*  958: 839 */     this.fill8l2.setVerticalAlignment(1);
/*  959: 840 */     this.fill8l2.setMaximumSize(new Dimension(18, 72));
/*  960: 841 */     this.fill8l2.setMinimumSize(new Dimension(8, 36));
/*  961: 842 */     this.fill8l2.setOpaque(true);
/*  962: 843 */     this.fill8l2.setPreferredSize(new Dimension(18, 72));
/*  963: 844 */     gridBagConstraints = new GridBagConstraints();
/*  964: 845 */     gridBagConstraints.gridx = 1;
/*  965: 846 */     gridBagConstraints.gridy = 0;
/*  966: 847 */     this.panel8Switches.add(this.fill8l2, gridBagConstraints);
/*  967:     */     
/*  968: 849 */     this.fill8l3.setBackground(new Color(0, 0, 0));
/*  969: 850 */     this.fill8l3.setFont(new Font("Dialog", 0, 7));
/*  970: 851 */     this.fill8l3.setForeground(new Color(255, 255, 255));
/*  971: 852 */     this.fill8l3.setHorizontalAlignment(0);
/*  972: 853 */     this.fill8l3.setVerticalAlignment(1);
/*  973: 854 */     this.fill8l3.setMaximumSize(new Dimension(18, 72));
/*  974: 855 */     this.fill8l3.setMinimumSize(new Dimension(8, 36));
/*  975: 856 */     this.fill8l3.setOpaque(true);
/*  976: 857 */     this.fill8l3.setPreferredSize(new Dimension(18, 72));
/*  977: 858 */     gridBagConstraints = new GridBagConstraints();
/*  978: 859 */     gridBagConstraints.gridx = 3;
/*  979: 860 */     gridBagConstraints.gridy = 0;
/*  980: 861 */     this.panel8Switches.add(this.fill8l3, gridBagConstraints);
/*  981:     */     
/*  982: 863 */     this.panelSwitch.setBackground(new Color(0, 0, 0));
/*  983: 864 */     this.panelSwitch.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)), BorderFactory.createLineBorder(new Color(255, 255, 255)))));
/*  984: 865 */     this.panelSwitch.setMaximumSize(new Dimension(32, 72));
/*  985: 866 */     this.panelSwitch.setMinimumSize(new Dimension(16, 36));
/*  986: 867 */     this.panelSwitch.setLayout(new GridBagLayout());
/*  987:     */     
/*  988: 869 */     this.sw.setMaximumSize(new Dimension(24, 44));
/*  989: 870 */     this.sw.setMinimumSize(new Dimension(12, 22));
/*  990: 871 */     this.sw.setName("sw");
/*  991: 872 */     this.sw.setPreferredSize(new Dimension(24, 44));
/*  992: 873 */     this.sw.setToggle(true);
/*  993: 874 */     gridBagConstraints = new GridBagConstraints();
/*  994: 875 */     gridBagConstraints.gridx = 0;
/*  995: 876 */     gridBagConstraints.gridy = 1;
/*  996: 877 */     this.panelSwitch.add(this.sw, gridBagConstraints);
/*  997:     */     
/*  998: 879 */     this.textSwitch.setBackground(new Color(193, 132, 29));
/*  999: 880 */     this.textSwitch.setFont(new Font("Dialog", 0, 8));
/* 1000: 881 */     this.textSwitch.setForeground(new Color(255, 255, 255));
/* 1001: 882 */     this.textSwitch.setHorizontalAlignment(0);
/* 1002: 883 */     this.textSwitch.setText("SW");
/* 1003: 884 */     this.textSwitch.setToolTipText("SWITCH");
/* 1004: 885 */     this.textSwitch.setMaximumSize(new Dimension(24, 22));
/* 1005: 886 */     this.textSwitch.setMinimumSize(new Dimension(12, 11));
/* 1006: 887 */     this.textSwitch.setOpaque(true);
/* 1007: 888 */     this.textSwitch.setPreferredSize(new Dimension(24, 22));
/* 1008: 889 */     gridBagConstraints = new GridBagConstraints();
/* 1009: 890 */     gridBagConstraints.gridx = 0;
/* 1010: 891 */     gridBagConstraints.gridy = 0;
/* 1011: 892 */     this.panelSwitch.add(this.textSwitch, gridBagConstraints);
/* 1012:     */     
/* 1013: 894 */     gridBagConstraints = new GridBagConstraints();
/* 1014: 895 */     gridBagConstraints.gridx = 2;
/* 1015: 896 */     gridBagConstraints.gridy = 0;
/* 1016: 897 */     this.panel8Switches.add(this.panelSwitch, gridBagConstraints);
/* 1017:     */     
/* 1018: 899 */     this.panelAlEal.setBackground(new Color(0, 0, 0));
/* 1019: 900 */     this.panelAlEal.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)), BorderFactory.createLineBorder(new Color(255, 255, 255)))));
/* 1020: 901 */     this.panelAlEal.setMaximumSize(new Dimension(58, 72));
/* 1021: 902 */     this.panelAlEal.setMinimumSize(new Dimension(29, 36));
/* 1022: 903 */     this.panelAlEal.setLayout(new GridBagLayout());
/* 1023:     */     
/* 1024: 905 */     this.textAddrLoad.setBackground(new Color(193, 77, 28));
/* 1025: 906 */     this.textAddrLoad.setFont(new Font("Dialog", 0, 8));
/* 1026: 907 */     this.textAddrLoad.setForeground(new Color(255, 255, 255));
/* 1027: 908 */     this.textAddrLoad.setHorizontalAlignment(0);
/* 1028: 909 */     this.textAddrLoad.setText("AL");
/* 1029: 910 */     this.textAddrLoad.setToolTipText("ADDR LOAD");
/* 1030: 911 */     this.textAddrLoad.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/* 1031: 912 */     this.textAddrLoad.setMaximumSize(new Dimension(26, 22));
/* 1032: 913 */     this.textAddrLoad.setMinimumSize(new Dimension(12, 11));
/* 1033: 914 */     this.textAddrLoad.setOpaque(true);
/* 1034: 915 */     this.textAddrLoad.setPreferredSize(new Dimension(24, 22));
/* 1035: 916 */     gridBagConstraints = new GridBagConstraints();
/* 1036: 917 */     gridBagConstraints.gridy = 0;
/* 1037: 918 */     this.panelAlEal.add(this.textAddrLoad, gridBagConstraints);
/* 1038:     */     
/* 1039: 920 */     this.swAl.setBackground(new Color(193, 77, 28));
/* 1040: 921 */     this.swAl.setMaximumSize(new Dimension(24, 44));
/* 1041: 922 */     this.swAl.setMinimumSize(new Dimension(12, 22));
/* 1042: 923 */     this.swAl.setName("al");
/* 1043: 924 */     this.swAl.setPreferredSize(new Dimension(24, 44));
/* 1044: 925 */     gridBagConstraints = new GridBagConstraints();
/* 1045: 926 */     gridBagConstraints.gridy = 1;
/* 1046: 927 */     this.panelAlEal.add(this.swAl, gridBagConstraints);
/* 1047:     */     
/* 1048: 929 */     this.textExtdAddrLoad.setBackground(new Color(193, 132, 29));
/* 1049: 930 */     this.textExtdAddrLoad.setFont(new Font("Dialog", 0, 8));
/* 1050: 931 */     this.textExtdAddrLoad.setForeground(new Color(255, 255, 255));
/* 1051: 932 */     this.textExtdAddrLoad.setHorizontalAlignment(0);
/* 1052: 933 */     this.textExtdAddrLoad.setText("EAL");
/* 1053: 934 */     this.textExtdAddrLoad.setToolTipText("EXTD ADDR LOAD");
/* 1054: 935 */     this.textExtdAddrLoad.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/* 1055: 936 */     this.textExtdAddrLoad.setMaximumSize(new Dimension(24, 22));
/* 1056: 937 */     this.textExtdAddrLoad.setMinimumSize(new Dimension(12, 11));
/* 1057: 938 */     this.textExtdAddrLoad.setOpaque(true);
/* 1058: 939 */     this.textExtdAddrLoad.setPreferredSize(new Dimension(24, 22));
/* 1059: 940 */     gridBagConstraints = new GridBagConstraints();
/* 1060: 941 */     gridBagConstraints.gridy = 0;
/* 1061: 942 */     this.panelAlEal.add(this.textExtdAddrLoad, gridBagConstraints);
/* 1062:     */     
/* 1063: 944 */     this.swEal.setMaximumSize(new Dimension(24, 44));
/* 1064: 945 */     this.swEal.setMinimumSize(new Dimension(12, 22));
/* 1065: 946 */     this.swEal.setName("eal");
/* 1066: 947 */     this.swEal.setPreferredSize(new Dimension(24, 44));
/* 1067: 948 */     gridBagConstraints = new GridBagConstraints();
/* 1068: 949 */     gridBagConstraints.gridy = 1;
/* 1069: 950 */     this.panelAlEal.add(this.swEal, gridBagConstraints);
/* 1070:     */     
/* 1071: 952 */     gridBagConstraints = new GridBagConstraints();
/* 1072: 953 */     gridBagConstraints.gridx = 6;
/* 1073: 954 */     gridBagConstraints.gridy = 0;
/* 1074: 955 */     this.panel8Switches.add(this.panelAlEal, gridBagConstraints);
/* 1075:     */     
/* 1076: 957 */     this.fill8r1.setBackground(new Color(0, 0, 0));
/* 1077: 958 */     this.fill8r1.setFont(new Font("Dialog", 0, 7));
/* 1078: 959 */     this.fill8r1.setForeground(new Color(255, 255, 255));
/* 1079: 960 */     this.fill8r1.setHorizontalAlignment(0);
/* 1080: 961 */     this.fill8r1.setVerticalAlignment(1);
/* 1081: 962 */     this.fill8r1.setMaximumSize(new Dimension(16, 72));
/* 1082: 963 */     this.fill8r1.setMinimumSize(new Dimension(6, 36));
/* 1083: 964 */     this.fill8r1.setPreferredSize(new Dimension(16, 72));
/* 1084: 965 */     this.fill8r1.setOpaque(true);
/* 1085: 966 */     gridBagConstraints = new GridBagConstraints();
/* 1086: 967 */     gridBagConstraints.gridx = 5;
/* 1087: 968 */     gridBagConstraints.gridy = 0;
/* 1088: 969 */     this.panel8Switches.add(this.fill8r1, gridBagConstraints);
/* 1089:     */     
/* 1090: 971 */     this.panelStartExamHaltSS.setBackground(new Color(0, 0, 0));
/* 1091: 972 */     this.panelStartExamHaltSS.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)), BorderFactory.createLineBorder(new Color(255, 255, 255)))));
/* 1092: 973 */     this.panelStartExamHaltSS.setMaximumSize(new Dimension(136, 72));
/* 1093: 974 */     this.panelStartExamHaltSS.setMinimumSize(new Dimension(68, 36));
/* 1094: 975 */     this.panelStartExamHaltSS.setLayout(new GridBagLayout());
/* 1095:     */     
/* 1096: 977 */     this.textStart.setBackground(new Color(193, 77, 28));
/* 1097: 978 */     this.textStart.setFont(new Font("Dialog", 0, 8));
/* 1098: 979 */     this.textStart.setForeground(new Color(255, 255, 255));
/* 1099: 980 */     this.textStart.setHorizontalAlignment(0);
/* 1100: 981 */     this.textStart.setText("START");
/* 1101: 982 */     this.textStart.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/* 1102: 983 */     this.textStart.setHorizontalTextPosition(0);
/* 1103: 984 */     this.textStart.setMaximumSize(new Dimension(48, 10));
/* 1104: 985 */     this.textStart.setMinimumSize(new Dimension(24, 5));
/* 1105: 986 */     this.textStart.setOpaque(true);
/* 1106: 987 */     this.textStart.setPreferredSize(new Dimension(48, 10));
/* 1107: 988 */     gridBagConstraints = new GridBagConstraints();
/* 1108: 989 */     gridBagConstraints.gridx = 0;
/* 1109: 990 */     gridBagConstraints.gridy = 0;
/* 1110: 991 */     gridBagConstraints.gridwidth = 2;
/* 1111: 992 */     this.panelStartExamHaltSS.add(this.textStart, gridBagConstraints);
/* 1112:     */     
/* 1113: 994 */     this.textClear.setBackground(new Color(193, 77, 28));
/* 1114: 995 */     this.textClear.setFont(new Font("Dialog", 0, 8));
/* 1115: 996 */     this.textClear.setForeground(new Color(255, 255, 255));
/* 1116: 997 */     this.textClear.setHorizontalAlignment(0);
/* 1117: 998 */     this.textClear.setText("CL");
/* 1118: 999 */     this.textClear.setToolTipText("CLEAR");
/* 1119:1000 */     this.textClear.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/* 1120:1001 */     this.textClear.setMaximumSize(new Dimension(24, 12));
/* 1121:1002 */     this.textClear.setMinimumSize(new Dimension(12, 6));
/* 1122:1003 */     this.textClear.setOpaque(true);
/* 1123:1004 */     this.textClear.setPreferredSize(new Dimension(24, 12));
/* 1124:1005 */     gridBagConstraints = new GridBagConstraints();
/* 1125:1006 */     gridBagConstraints.gridx = 0;
/* 1126:1007 */     gridBagConstraints.gridy = 1;
/* 1127:1008 */     this.panelStartExamHaltSS.add(this.textClear, gridBagConstraints);
/* 1128:     */     
/* 1129:1010 */     this.textCont.setBackground(new Color(193, 132, 29));
/* 1130:1011 */     this.textCont.setFont(new Font("Dialog", 0, 8));
/* 1131:1012 */     this.textCont.setForeground(new Color(255, 255, 255));
/* 1132:1013 */     this.textCont.setHorizontalAlignment(0);
/* 1133:1014 */     this.textCont.setText("CO");
/* 1134:1015 */     this.textCont.setToolTipText("CONT");
/* 1135:1016 */     this.textCont.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/* 1136:1017 */     this.textCont.setMaximumSize(new Dimension(24, 12));
/* 1137:1018 */     this.textCont.setMinimumSize(new Dimension(12, 6));
/* 1138:1019 */     this.textCont.setOpaque(true);
/* 1139:1020 */     this.textCont.setPreferredSize(new Dimension(24, 12));
/* 1140:1021 */     gridBagConstraints = new GridBagConstraints();
/* 1141:1022 */     gridBagConstraints.gridx = 1;
/* 1142:1023 */     gridBagConstraints.gridy = 1;
/* 1143:1024 */     this.panelStartExamHaltSS.add(this.textCont, gridBagConstraints);
/* 1144:     */     
/* 1145:1026 */     this.swClear.setBackground(new Color(193, 77, 28));
/* 1146:1027 */     this.swClear.setMaximumSize(new Dimension(24, 44));
/* 1147:1028 */     this.swClear.setMinimumSize(new Dimension(12, 22));
/* 1148:1029 */     this.swClear.setName("clear");
/* 1149:1030 */     this.swClear.setPreferredSize(new Dimension(24, 44));
/* 1150:1031 */     gridBagConstraints = new GridBagConstraints();
/* 1151:1032 */     gridBagConstraints.gridx = 0;
/* 1152:1033 */     gridBagConstraints.gridy = 2;
/* 1153:1034 */     this.panelStartExamHaltSS.add(this.swClear, gridBagConstraints);
/* 1154:     */     
/* 1155:1036 */     this.swCont.setMaximumSize(new Dimension(24, 44));
/* 1156:1037 */     this.swCont.setMinimumSize(new Dimension(12, 22));
/* 1157:1038 */     this.swCont.setName("cont");
/* 1158:1039 */     this.swCont.setPreferredSize(new Dimension(24, 44));
/* 1159:1040 */     gridBagConstraints = new GridBagConstraints();
/* 1160:1041 */     gridBagConstraints.gridx = 1;
/* 1161:1042 */     gridBagConstraints.gridy = 2;
/* 1162:1043 */     this.panelStartExamHaltSS.add(this.swCont, gridBagConstraints);
/* 1163:     */     
/* 1164:1045 */     this.textExam.setBackground(new Color(193, 77, 28));
/* 1165:1046 */     this.textExam.setFont(new Font("Dialog", 0, 8));
/* 1166:1047 */     this.textExam.setForeground(new Color(255, 255, 255));
/* 1167:1048 */     this.textExam.setHorizontalAlignment(0);
/* 1168:1049 */     this.textExam.setText("EXAM");
/* 1169:1050 */     this.textExam.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/* 1170:1051 */     this.textExam.setMaximumSize(new Dimension(24, 22));
/* 1171:1052 */     this.textExam.setMinimumSize(new Dimension(12, 11));
/* 1172:1053 */     this.textExam.setOpaque(true);
/* 1173:1054 */     this.textExam.setPreferredSize(new Dimension(24, 22));
/* 1174:1055 */     gridBagConstraints = new GridBagConstraints();
/* 1175:1056 */     gridBagConstraints.gridy = 0;
/* 1176:1057 */     gridBagConstraints.gridheight = 2;
/* 1177:1058 */     this.panelStartExamHaltSS.add(this.textExam, gridBagConstraints);
/* 1178:     */     
/* 1179:1060 */     this.swSingStep.setBackground(new Color(193, 77, 28));
/* 1180:1061 */     this.swSingStep.setMaximumSize(new Dimension(24, 44));
/* 1181:1062 */     this.swSingStep.setMinimumSize(new Dimension(12, 22));
/* 1182:1063 */     this.swSingStep.setName("singstep");
/* 1183:1064 */     this.swSingStep.setPreferredSize(new Dimension(24, 44));
/* 1184:1065 */     this.swSingStep.setToggle(true);
/* 1185:1066 */     gridBagConstraints = new GridBagConstraints();
/* 1186:1067 */     gridBagConstraints.gridx = 4;
/* 1187:1068 */     gridBagConstraints.gridy = 2;
/* 1188:1069 */     this.panelStartExamHaltSS.add(this.swSingStep, gridBagConstraints);
/* 1189:     */     
/* 1190:1071 */     this.textHalt.setBackground(new Color(193, 132, 29));
/* 1191:1072 */     this.textHalt.setFont(new Font("Dialog", 0, 8));
/* 1192:1073 */     this.textHalt.setForeground(new Color(255, 255, 255));
/* 1193:1074 */     this.textHalt.setHorizontalAlignment(0);
/* 1194:1075 */     this.textHalt.setText("HALT");
/* 1195:1076 */     this.textHalt.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/* 1196:1077 */     this.textHalt.setMaximumSize(new Dimension(24, 22));
/* 1197:1078 */     this.textHalt.setMinimumSize(new Dimension(12, 11));
/* 1198:1079 */     this.textHalt.setOpaque(true);
/* 1199:1080 */     this.textHalt.setPreferredSize(new Dimension(24, 22));
/* 1200:1081 */     gridBagConstraints = new GridBagConstraints();
/* 1201:1082 */     gridBagConstraints.gridy = 0;
/* 1202:1083 */     gridBagConstraints.gridheight = 2;
/* 1203:1084 */     this.panelStartExamHaltSS.add(this.textHalt, gridBagConstraints);
/* 1204:     */     
/* 1205:1086 */     this.swHalt.setMaximumSize(new Dimension(24, 44));
/* 1206:1087 */     this.swHalt.setMinimumSize(new Dimension(12, 22));
/* 1207:1088 */     this.swHalt.setName("halt");
/* 1208:1089 */     this.swHalt.setPreferredSize(new Dimension(24, 44));
/* 1209:1090 */     this.swHalt.setToggle(true);
/* 1210:1091 */     gridBagConstraints = new GridBagConstraints();
/* 1211:1092 */     gridBagConstraints.gridx = 3;
/* 1212:1093 */     gridBagConstraints.gridy = 2;
/* 1213:1094 */     this.panelStartExamHaltSS.add(this.swHalt, gridBagConstraints);
/* 1214:     */     
/* 1215:1096 */     this.textSingStep.setBackground(new Color(193, 77, 28));
/* 1216:1097 */     this.textSingStep.setFont(new Font("Dialog", 0, 8));
/* 1217:1098 */     this.textSingStep.setForeground(new Color(255, 255, 255));
/* 1218:1099 */     this.textSingStep.setHorizontalAlignment(0);
/* 1219:1100 */     this.textSingStep.setText("SS");
/* 1220:1101 */     this.textSingStep.setToolTipText("SING STEP");
/* 1221:1102 */     this.textSingStep.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/* 1222:1103 */     this.textSingStep.setMaximumSize(new Dimension(24, 22));
/* 1223:1104 */     this.textSingStep.setMinimumSize(new Dimension(12, 10));
/* 1224:1105 */     this.textSingStep.setOpaque(true);
/* 1225:1106 */     this.textSingStep.setPreferredSize(new Dimension(24, 22));
/* 1226:1107 */     gridBagConstraints = new GridBagConstraints();
/* 1227:1108 */     gridBagConstraints.gridy = 0;
/* 1228:1109 */     gridBagConstraints.gridheight = 2;
/* 1229:1110 */     this.panelStartExamHaltSS.add(this.textSingStep, gridBagConstraints);
/* 1230:     */     
/* 1231:1112 */     this.swExam.setBackground(new Color(193, 77, 28));
/* 1232:1113 */     this.swExam.setMaximumSize(new Dimension(24, 44));
/* 1233:1114 */     this.swExam.setMinimumSize(new Dimension(12, 22));
/* 1234:1115 */     this.swExam.setName("exam");
/* 1235:1116 */     this.swExam.setPreferredSize(new Dimension(24, 44));
/* 1236:1117 */     gridBagConstraints = new GridBagConstraints();
/* 1237:1118 */     gridBagConstraints.gridx = 2;
/* 1238:1119 */     gridBagConstraints.gridy = 2;
/* 1239:1120 */     this.panelStartExamHaltSS.add(this.swExam, gridBagConstraints);
/* 1240:     */     
/* 1241:1122 */     gridBagConstraints = new GridBagConstraints();
/* 1242:1123 */     gridBagConstraints.gridx = 8;
/* 1243:1124 */     gridBagConstraints.gridy = 0;
/* 1244:1125 */     this.panel8Switches.add(this.panelStartExamHaltSS, gridBagConstraints);
/* 1245:     */     
/* 1246:1127 */     this.fill8r2.setBackground(new Color(0, 0, 0));
/* 1247:1128 */     this.fill8r2.setFont(new Font("Dialog", 0, 7));
/* 1248:1129 */     this.fill8r2.setForeground(new Color(255, 255, 255));
/* 1249:1130 */     this.fill8r2.setHorizontalAlignment(0);
/* 1250:1131 */     this.fill8r2.setVerticalAlignment(1);
/* 1251:1132 */     this.fill8r2.setMaximumSize(new Dimension(16, 72));
/* 1252:1133 */     this.fill8r2.setMinimumSize(new Dimension(6, 36));
/* 1253:1134 */     this.fill8r2.setPreferredSize(new Dimension(16, 72));
/* 1254:1135 */     this.fill8r2.setOpaque(true);
/* 1255:1136 */     gridBagConstraints = new GridBagConstraints();
/* 1256:1137 */     gridBagConstraints.gridx = 7;
/* 1257:1138 */     gridBagConstraints.gridy = 0;
/* 1258:1139 */     this.panel8Switches.add(this.fill8r2, gridBagConstraints);
/* 1259:     */     
/* 1260:1141 */     this.panelDep.setBackground(new Color(0, 0, 0));
/* 1261:1142 */     this.panelDep.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)), BorderFactory.createLineBorder(new Color(255, 255, 255)))));
/* 1262:1143 */     this.panelDep.setMaximumSize(new Dimension(32, 72));
/* 1263:1144 */     this.panelDep.setMinimumSize(new Dimension(16, 36));
/* 1264:1145 */     this.panelDep.setLayout(new GridBagLayout());
/* 1265:     */     
/* 1266:1147 */     this.textDep.setBackground(new Color(193, 132, 29));
/* 1267:1148 */     this.textDep.setFont(new Font("Dialog", 0, 8));
/* 1268:1149 */     this.textDep.setForeground(new Color(255, 255, 255));
/* 1269:1150 */     this.textDep.setHorizontalAlignment(0);
/* 1270:1151 */     this.textDep.setText("DEP");
/* 1271:1152 */     this.textDep.setToolTipText("DEPOSIT");
/* 1272:1153 */     this.textDep.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
/* 1273:1154 */     this.textDep.setMaximumSize(new Dimension(24, 22));
/* 1274:1155 */     this.textDep.setMinimumSize(new Dimension(12, 11));
/* 1275:1156 */     this.textDep.setOpaque(true);
/* 1276:1157 */     this.textDep.setPreferredSize(new Dimension(24, 22));
/* 1277:1158 */     gridBagConstraints = new GridBagConstraints();
/* 1278:1159 */     gridBagConstraints.gridy = 0;
/* 1279:1160 */     this.panelDep.add(this.textDep, gridBagConstraints);
/* 1280:     */     
/* 1281:1162 */     this.swDep.setInitial(false);
/* 1282:1163 */     this.swDep.setMaximumSize(new Dimension(24, 44));
/* 1283:1164 */     this.swDep.setMinimumSize(new Dimension(12, 22));
/* 1284:1165 */     this.swDep.setName("dep");
/* 1285:1166 */     this.swDep.setPreferredSize(new Dimension(24, 44));
/* 1286:1167 */     gridBagConstraints = new GridBagConstraints();
/* 1287:1168 */     gridBagConstraints.gridx = 0;
/* 1288:1169 */     gridBagConstraints.gridy = 1;
/* 1289:1170 */     this.panelDep.add(this.swDep, gridBagConstraints);
/* 1290:     */     
/* 1291:1172 */     gridBagConstraints = new GridBagConstraints();
/* 1292:1173 */     gridBagConstraints.gridx = 10;
/* 1293:1174 */     gridBagConstraints.gridy = 0;
/* 1294:1175 */     this.panel8Switches.add(this.panelDep, gridBagConstraints);
/* 1295:     */     
/* 1296:1177 */     this.swReg.setToolTipText("");
/* 1297:1178 */     this.swReg.setMaximumSize(new Dimension(318, 72));
/* 1298:1179 */     this.swReg.setName("swr");
/* 1299:1180 */     gridBagConstraints = new GridBagConstraints();
/* 1300:1181 */     gridBagConstraints.gridx = 4;
/* 1301:1182 */     gridBagConstraints.gridy = 0;
/* 1302:1183 */     this.panel8Switches.add(this.swReg, gridBagConstraints);
/* 1303:     */     
/* 1304:1185 */     add(this.panel8Switches, new GridBagConstraints());
/* 1305:     */     
/* 1306:1187 */     this.fill8r4.setBackground(new Color(0, 0, 0));
/* 1307:1188 */     this.fill8r4.setMaximumSize(new Dimension(36, 72));
/* 1308:1189 */     this.fill8r4.setMinimumSize(new Dimension(18, 36));
/* 1309:1190 */     this.fill8r4.setPreferredSize(new Dimension(36, 72));
/* 1310:1191 */     this.fill8r4.setOpaque(true);
/* 1311:1192 */     gridBagConstraints = new GridBagConstraints();
/* 1312:1193 */     gridBagConstraints.gridwidth = 0;
/* 1313:1194 */     gridBagConstraints.anchor = 13;
/* 1314:1195 */     add(this.fill8r4, gridBagConstraints);
/* 1315:     */     
/* 1316:1197 */     this.fill9.setBackground(new Color(0, 0, 0));
/* 1317:1198 */     this.fill9.setMaximumSize(new Dimension(800, 32));
/* 1318:1199 */     this.fill9.setMinimumSize(new Dimension(400, 16));
/* 1319:1200 */     this.fill9.setPreferredSize(new Dimension(800, 32));
/* 1320:1201 */     this.fill9.setOpaque(true);
/* 1321:1202 */     gridBagConstraints = new GridBagConstraints();
/* 1322:1203 */     gridBagConstraints.gridx = 0;
/* 1323:1204 */     gridBagConstraints.gridy = 7;
/* 1324:1205 */     gridBagConstraints.gridwidth = 3;
/* 1325:1206 */     gridBagConstraints.anchor = 15;
/* 1326:1207 */     add(this.fill9, gridBagConstraints);
/* 1327:     */   }
/* 1328:     */   
/* 1329:     */   public void setLogic(PanelLogic panelLogic)
/* 1330:     */   {
/* 1331:1213 */     this.panelLogic = panelLogic;
/* 1332:     */   }
/* 1333:     */   
/* 1334:     */   private void fill1ComponentResized(ComponentEvent evt)
/* 1335:     */   {
/* 1336:1220 */     if (this.fill1.getWidth() < 500)
/* 1337:     */     {
/* 1338:1221 */       this.text2Type.setFont(new Font("SansSerif", 1, 10));
/* 1339:1222 */       this.text3Company.setFont(new Font("SansSerif", 0, 5));
/* 1340:1223 */       this.text4MemAddr.setFont(new Font("SansSerif", 0, 4));
/* 1341:1224 */       this.text4Ema.setFont(new Font("Dialog", 0, 4));
/* 1342:1225 */       this.text4Run.setFont(new Font("Dialog", 0, 4));
/* 1343:1226 */       this.textState.setFont(new Font("Dialog", 0, 4));
/* 1344:1227 */       this.textStatus.setFont(new Font("Dialog", 0, 4));
/* 1345:1228 */       this.textRegAc.setFont(new Font("Dialog", 0, 4));
/* 1346:1229 */       this.textRegMd.setFont(new Font("Dialog", 0, 4));
/* 1347:1230 */       this.textRegMq.setFont(new Font("Dialog", 0, 4));
/* 1348:1231 */       this.textRegBus.setFont(new Font("Dialog", 0, 4));
/* 1349:1232 */       this.textOff.setFont(new Font("Dialog", 0, 4));
/* 1350:1233 */       this.textPower.setFont(new Font("Dialog", 0, 4));
/* 1351:1234 */       this.textLock.setFont(new Font("Dialog", 0, 4));
/* 1352:1235 */       this.textSwitch.setFont(new Font("Dialog", 0, 4));
/* 1353:1236 */       this.textAddrLoad.setFont(new Font("Dialog", 0, 4));
/* 1354:1237 */       this.textExtdAddrLoad.setFont(new Font("Dialog", 0, 4));
/* 1355:1238 */       this.textStart.setFont(new Font("Dialog", 0, 4));
/* 1356:1239 */       this.textClear.setFont(new Font("Dialog", 0, 4));
/* 1357:1240 */       this.textCont.setFont(new Font("Dialog", 0, 4));
/* 1358:1241 */       this.textExam.setFont(new Font("Dialog", 0, 4));
/* 1359:1242 */       this.textDep.setFont(new Font("Dialog", 0, 4));
/* 1360:1243 */       this.textHalt.setFont(new Font("Dialog", 0, 4));
/* 1361:1244 */       this.textSingStep.setFont(new Font("Dialog", 0, 4));
/* 1362:     */     }
/* 1363:     */     else
/* 1364:     */     {
/* 1365:1247 */       this.text2Type.setFont(new Font("SansSerif", 1, 20));
/* 1366:1248 */       this.text3Company.setFont(new Font("SansSerif", 0, 10));
/* 1367:1249 */       this.text4MemAddr.setFont(new Font("SansSerif", 0, 8));
/* 1368:1250 */       this.text4Ema.setFont(new Font("Dialog", 0, 8));
/* 1369:1251 */       this.text4Run.setFont(new Font("Dialog", 0, 8));
/* 1370:1252 */       this.textState.setFont(new Font("Dialog", 0, 8));
/* 1371:1253 */       this.textStatus.setFont(new Font("Dialog", 0, 8));
/* 1372:1254 */       this.textRegAc.setFont(new Font("Dialog", 0, 8));
/* 1373:1255 */       this.textRegMd.setFont(new Font("Dialog", 0, 8));
/* 1374:1256 */       this.textRegMq.setFont(new Font("Dialog", 0, 8));
/* 1375:1257 */       this.textRegBus.setFont(new Font("Dialog", 0, 8));
/* 1376:1258 */       this.textOff.setFont(new Font("Dialog", 0, 8));
/* 1377:1259 */       this.textPower.setFont(new Font("Dialog", 0, 8));
/* 1378:1260 */       this.textLock.setFont(new Font("Dialog", 0, 8));
/* 1379:1261 */       this.textSwitch.setFont(new Font("Dialog", 0, 8));
/* 1380:1262 */       this.textAddrLoad.setFont(new Font("Dialog", 0, 8));
/* 1381:1263 */       this.textExtdAddrLoad.setFont(new Font("Dialog", 0, 8));
/* 1382:1264 */       this.textStart.setFont(new Font("Dialog", 0, 8));
/* 1383:1265 */       this.textClear.setFont(new Font("Dialog", 0, 8));
/* 1384:1266 */       this.textCont.setFont(new Font("Dialog", 0, 8));
/* 1385:1267 */       this.textExam.setFont(new Font("Dialog", 0, 8));
/* 1386:1268 */       this.textDep.setFont(new Font("Dialog", 0, 8));
/* 1387:1269 */       this.textHalt.setFont(new Font("Dialog", 0, 8));
/* 1388:1270 */       this.textSingStep.setFont(new Font("Dialog", 0, 8));
/* 1389:     */     }
/* 1390:     */   }
/* 1391:     */   
/* 1392:     */   public void mouseClicked(MouseEvent e)
/* 1393:     */   {
/* 1394:1276 */     if (e.isPopupTrigger()) {
/* 1395:1277 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/* 1396:     */     }
/* 1397:     */   }
/* 1398:     */   
/* 1399:     */   public void mouseEntered(MouseEvent e) {}
/* 1400:     */   
/* 1401:     */   public void mouseExited(MouseEvent e) {}
/* 1402:     */   
/* 1403:     */   public void mousePressed(MouseEvent e)
/* 1404:     */   {
/* 1405:1288 */     if (e.isPopupTrigger()) {
/* 1406:1289 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/* 1407:     */     }
/* 1408:     */   }
/* 1409:     */   
/* 1410:     */   public void mouseReleased(MouseEvent e)
/* 1411:     */   {
/* 1412:1294 */     if (e.isPopupTrigger()) {
/* 1413:1295 */       this.jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
/* 1414:     */     }
/* 1415:     */   }
/* 1416:     */   
/* 1417:     */   public void actionPerformed(ActionEvent e)
/* 1418:     */   {
/* 1419:1300 */     AbstractButton x = (AbstractButton)e.getSource();
/* 1420:1301 */     Integer val = new Integer(Integer.parseInt(x.getName()));
/* 1421:1302 */     this.panelLogic.menuContext(val.intValue());
/* 1422:1303 */     repaint();
/* 1423:     */   }
/* 1424:     */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.Panel8
 * JD-Core Version:    0.7.0.1
 */