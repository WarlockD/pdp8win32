/*   1:    */ package Devices;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.GridBagConstraints;
/*   7:    */ import java.awt.GridBagLayout;
/*   8:    */ import javax.swing.JComponent;
/*   9:    */ import javax.swing.JLabel;
/*  10:    */ import javax.swing.JPanel;
/*  11:    */ 
/*  12:    */ public class VT100Lamps
/*  13:    */   extends JPanel
/*  14:    */ {
/*  15:    */   public JPanel CenterPanel;
/*  16:    */   private JLabel Digital;
/*  17:    */   private JLabel Left;
/*  18:    */   private JLabel LowerText;
/*  19:    */   private JLabel Right;
/*  20:    */   private JLabel Upper;
/*  21:    */   private JLabel VT100;
/*  22:    */   private Lamp lamp1;
/*  23:    */   private Lamp lamp2;
/*  24:    */   private Lamp lamp3;
/*  25:    */   private Lamp lamp4;
/*  26:    */   private Lamp lamp5;
/*  27:    */   private Lamp lamp6;
/*  28:    */   private Lamp lamp7;
/*  29:    */   
/*  30:    */   public VT100Lamps()
/*  31:    */   {
/*  32: 18 */     initComponents();
/*  33:    */   }
/*  34:    */   
/*  35:    */   private void initComponents()
/*  36:    */   {
/*  37: 30 */     this.lamp1 = new Lamp();
/*  38: 31 */     this.lamp2 = new Lamp();
/*  39: 32 */     this.lamp3 = new Lamp();
/*  40: 33 */     this.lamp4 = new Lamp();
/*  41: 34 */     this.lamp5 = new Lamp();
/*  42: 35 */     this.lamp6 = new Lamp();
/*  43: 36 */     this.lamp7 = new Lamp();
/*  44: 37 */     this.Digital = new JLabel();
/*  45: 38 */     this.VT100 = new JLabel();
/*  46: 39 */     this.Right = new JLabel();
/*  47: 40 */     this.Left = new JLabel();
/*  48: 41 */     this.Upper = new JLabel();
/*  49: 42 */     this.CenterPanel = new JPanel();
/*  50: 43 */     this.LowerText = new JLabel();
/*  51:    */     
/*  52: 45 */     setBackground(new Color(153, 255, 153));
/*  53: 46 */     setMinimumSize(new Dimension(355, 225));
/*  54: 47 */     setPreferredSize(new Dimension(710, 450));
/*  55: 48 */     setLayout(new GridBagLayout());
/*  56:    */     
/*  57: 50 */     this.lamp1.setBackground(new Color(51, 51, 51));
/*  58: 51 */     this.lamp1.setForeground(new Color(51, 51, 51));
/*  59: 52 */     this.lamp1.setMaximumSize(new Dimension(30, 20));
/*  60: 53 */     this.lamp1.setMinimumSize(new Dimension(15, 10));
/*  61: 54 */     this.lamp1.setPreferredSize(new Dimension(30, 20));
/*  62: 55 */     this.lamp1.setState(false);
/*  63: 56 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*  64: 57 */     gridBagConstraints.gridx = 2;
/*  65: 58 */     gridBagConstraints.gridy = 2;
/*  66: 59 */     add(this.lamp1, gridBagConstraints);
/*  67:    */     
/*  68: 61 */     this.lamp2.setBackground(new Color(51, 51, 51));
/*  69: 62 */     this.lamp2.setForeground(new Color(51, 51, 51));
/*  70: 63 */     this.lamp2.setMaximumSize(new Dimension(30, 20));
/*  71: 64 */     this.lamp2.setMinimumSize(new Dimension(15, 10));
/*  72: 65 */     this.lamp2.setPreferredSize(new Dimension(30, 20));
/*  73: 66 */     this.lamp2.setState(false);
/*  74: 67 */     gridBagConstraints = new GridBagConstraints();
/*  75: 68 */     gridBagConstraints.gridx = 3;
/*  76: 69 */     gridBagConstraints.gridy = 2;
/*  77: 70 */     add(this.lamp2, gridBagConstraints);
/*  78:    */     
/*  79: 72 */     this.lamp3.setBackground(new Color(51, 51, 51));
/*  80: 73 */     this.lamp3.setForeground(new Color(51, 51, 51));
/*  81: 74 */     this.lamp3.setMaximumSize(new Dimension(30, 20));
/*  82: 75 */     this.lamp3.setMinimumSize(new Dimension(15, 10));
/*  83: 76 */     this.lamp3.setPreferredSize(new Dimension(30, 20));
/*  84: 77 */     this.lamp3.setState(false);
/*  85: 78 */     gridBagConstraints = new GridBagConstraints();
/*  86: 79 */     gridBagConstraints.gridx = 4;
/*  87: 80 */     gridBagConstraints.gridy = 2;
/*  88: 81 */     add(this.lamp3, gridBagConstraints);
/*  89:    */     
/*  90: 83 */     this.lamp4.setBackground(new Color(51, 51, 51));
/*  91: 84 */     this.lamp4.setForeground(new Color(51, 51, 51));
/*  92: 85 */     this.lamp4.setMaximumSize(new Dimension(30, 20));
/*  93: 86 */     this.lamp4.setMinimumSize(new Dimension(15, 10));
/*  94: 87 */     this.lamp4.setPreferredSize(new Dimension(30, 20));
/*  95: 88 */     this.lamp4.setState(false);
/*  96: 89 */     gridBagConstraints = new GridBagConstraints();
/*  97: 90 */     gridBagConstraints.gridx = 5;
/*  98: 91 */     gridBagConstraints.gridy = 2;
/*  99: 92 */     add(this.lamp4, gridBagConstraints);
/* 100:    */     
/* 101: 94 */     this.lamp5.setBackground(new Color(51, 51, 51));
/* 102: 95 */     this.lamp5.setForeground(new Color(51, 51, 51));
/* 103: 96 */     this.lamp5.setMaximumSize(new Dimension(30, 20));
/* 104: 97 */     this.lamp5.setMinimumSize(new Dimension(15, 10));
/* 105: 98 */     this.lamp5.setPreferredSize(new Dimension(30, 20));
/* 106: 99 */     this.lamp5.setState(false);
/* 107:100 */     gridBagConstraints = new GridBagConstraints();
/* 108:101 */     gridBagConstraints.gridx = 6;
/* 109:102 */     gridBagConstraints.gridy = 2;
/* 110:103 */     add(this.lamp5, gridBagConstraints);
/* 111:    */     
/* 112:105 */     this.lamp6.setBackground(new Color(51, 51, 51));
/* 113:106 */     this.lamp6.setForeground(new Color(51, 51, 51));
/* 114:107 */     this.lamp6.setMaximumSize(new Dimension(30, 20));
/* 115:108 */     this.lamp6.setMinimumSize(new Dimension(15, 10));
/* 116:109 */     this.lamp6.setPreferredSize(new Dimension(30, 20));
/* 117:110 */     this.lamp6.setState(false);
/* 118:111 */     gridBagConstraints = new GridBagConstraints();
/* 119:112 */     gridBagConstraints.gridx = 7;
/* 120:113 */     gridBagConstraints.gridy = 2;
/* 121:114 */     add(this.lamp6, gridBagConstraints);
/* 122:    */     
/* 123:116 */     this.lamp7.setBackground(new Color(51, 51, 51));
/* 124:117 */     this.lamp7.setForeground(new Color(51, 51, 51));
/* 125:118 */     this.lamp7.setMaximumSize(new Dimension(30, 20));
/* 126:119 */     this.lamp7.setMinimumSize(new Dimension(15, 10));
/* 127:120 */     this.lamp7.setPreferredSize(new Dimension(30, 20));
/* 128:121 */     this.lamp7.setState(false);
/* 129:122 */     gridBagConstraints = new GridBagConstraints();
/* 130:123 */     gridBagConstraints.gridx = 8;
/* 131:124 */     gridBagConstraints.gridy = 2;
/* 132:125 */     add(this.lamp7, gridBagConstraints);
/* 133:    */     
/* 134:127 */     this.Digital.setBackground(Color.darkGray);
/* 135:128 */     this.Digital.setFont(new Font("Dialog", 0, 18));
/* 136:129 */     this.Digital.setForeground(new Color(255, 255, 255));
/* 137:130 */     this.Digital.setHorizontalAlignment(0);
/* 138:131 */     this.Digital.setText("d|i|g|i|t|a|l");
/* 139:132 */     this.Digital.setMinimumSize(new Dimension(112, 20));
/* 140:133 */     this.Digital.setPreferredSize(new Dimension(224, 40));
/* 141:134 */     this.Digital.setOpaque(true);
/* 142:135 */     gridBagConstraints = new GridBagConstraints();
/* 143:136 */     gridBagConstraints.gridx = 1;
/* 144:137 */     gridBagConstraints.gridy = 2;
/* 145:138 */     gridBagConstraints.gridheight = 2;
/* 146:139 */     add(this.Digital, gridBagConstraints);
/* 147:    */     
/* 148:141 */     this.VT100.setBackground(Color.darkGray);
/* 149:142 */     this.VT100.setFont(new Font("Dialog", 0, 18));
/* 150:143 */     this.VT100.setForeground(new Color(204, 0, 0));
/* 151:144 */     this.VT100.setHorizontalAlignment(0);
/* 152:145 */     this.VT100.setText("  VT100");
/* 153:146 */     this.VT100.setMinimumSize(new Dimension(112, 20));
/* 154:147 */     this.VT100.setPreferredSize(new Dimension(224, 40));
/* 155:148 */     this.VT100.setOpaque(true);
/* 156:149 */     gridBagConstraints = new GridBagConstraints();
/* 157:150 */     gridBagConstraints.gridx = 9;
/* 158:151 */     gridBagConstraints.gridy = 2;
/* 159:152 */     gridBagConstraints.gridheight = 2;
/* 160:153 */     add(this.VT100, gridBagConstraints);
/* 161:    */     
/* 162:155 */     this.Right.setBackground(Color.darkGray);
/* 163:156 */     this.Right.setMinimumSize(new Dimension(10, 222));
/* 164:157 */     this.Right.setPreferredSize(new Dimension(20, 444));
/* 165:158 */     this.Right.setOpaque(true);
/* 166:159 */     gridBagConstraints = new GridBagConstraints();
/* 167:160 */     gridBagConstraints.gridx = 10;
/* 168:161 */     gridBagConstraints.gridy = 0;
/* 169:162 */     gridBagConstraints.gridheight = 4;
/* 170:163 */     gridBagConstraints.anchor = 13;
/* 171:164 */     add(this.Right, gridBagConstraints);
/* 172:    */     
/* 173:166 */     this.Left.setBackground(Color.darkGray);
/* 174:167 */     this.Left.setMinimumSize(new Dimension(10, 222));
/* 175:168 */     this.Left.setPreferredSize(new Dimension(20, 444));
/* 176:169 */     this.Left.setOpaque(true);
/* 177:170 */     gridBagConstraints = new GridBagConstraints();
/* 178:171 */     gridBagConstraints.gridx = 0;
/* 179:172 */     gridBagConstraints.gridy = 0;
/* 180:173 */     gridBagConstraints.gridheight = 4;
/* 181:174 */     gridBagConstraints.anchor = 17;
/* 182:175 */     add(this.Left, gridBagConstraints);
/* 183:    */     
/* 184:177 */     this.Upper.setBackground(Color.darkGray);
/* 185:178 */     this.Upper.setMinimumSize(new Dimension(329, 10));
/* 186:179 */     this.Upper.setPreferredSize(new Dimension(658, 20));
/* 187:180 */     this.Upper.setOpaque(true);
/* 188:181 */     gridBagConstraints = new GridBagConstraints();
/* 189:182 */     gridBagConstraints.gridx = 1;
/* 190:183 */     gridBagConstraints.gridy = 0;
/* 191:184 */     gridBagConstraints.gridwidth = 9;
/* 192:185 */     add(this.Upper, gridBagConstraints);
/* 193:    */     
/* 194:187 */     this.CenterPanel.setBackground(new Color(255, 255, 255));
/* 195:188 */     this.CenterPanel.setMinimumSize(new Dimension(329, 192));
/* 196:189 */     this.CenterPanel.setPreferredSize(new Dimension(658, 384));
/* 197:190 */     gridBagConstraints = new GridBagConstraints();
/* 198:191 */     gridBagConstraints.gridx = 1;
/* 199:192 */     gridBagConstraints.gridy = 1;
/* 200:193 */     gridBagConstraints.gridwidth = 9;
/* 201:194 */     add(this.CenterPanel, gridBagConstraints);
/* 202:    */     
/* 203:196 */     this.LowerText.setBackground(Color.darkGray);
/* 204:197 */     this.LowerText.setFont(new Font("Lucida Sans Unicode", 0, 11));
/* 205:198 */     this.LowerText.setForeground(new Color(255, 255, 255));
/* 206:199 */     this.LowerText.setHorizontalAlignment(2);
/* 207:200 */     this.LowerText.setText("online local  lock    L1      L2      L3     L4");
/* 208:201 */     this.LowerText.setMinimumSize(new Dimension(105, 10));
/* 209:202 */     this.LowerText.setPreferredSize(new Dimension(210, 20));
/* 210:203 */     this.LowerText.setOpaque(true);
/* 211:204 */     gridBagConstraints = new GridBagConstraints();
/* 212:205 */     gridBagConstraints.gridx = 2;
/* 213:206 */     gridBagConstraints.gridy = 3;
/* 214:207 */     gridBagConstraints.gridwidth = 7;
/* 215:208 */     add(this.LowerText, gridBagConstraints);
/* 216:    */   }
/* 217:    */   
/* 218:    */   public Dimension setPanel(JPanel panel)
/* 219:    */   {
/* 220:212 */     Dimension dimnew = panel.getPreferredSize();
/* 221:213 */     Dimension dimold = this.CenterPanel.getPreferredSize();
/* 222:214 */     Dimension dimback = new Dimension(0, 0);
/* 223:215 */     this.ratiox = (dimnew.getWidth() / dimold.getWidth());
/* 224:216 */     this.ratioy = (dimnew.getHeight() / dimold.getHeight());
/* 225:217 */     remove(this.CenterPanel);
/* 226:218 */     this.CenterPanel = panel;
/* 227:    */     
/* 228:220 */     this.CenterPanel.setPreferredSize(dimnew);
/* 229:221 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/* 230:222 */     gridBagConstraints.gridx = 1;
/* 231:223 */     gridBagConstraints.gridy = 1;
/* 232:224 */     gridBagConstraints.gridwidth = 9;
/* 233:225 */     add(this.CenterPanel, gridBagConstraints);
/* 234:226 */     setBackground(Color.darkGray);
/* 235:    */     
/* 236:228 */     adjust(this.Upper);
/* 237:229 */     double panelwidth = this.Upper.getSize().getWidth();
/* 238:230 */     adjust(this.Left);
/* 239:231 */     double panelheight = this.Left.getSize().getHeight();
/* 240:232 */     panelwidth += this.Left.getSize().getWidth() * 2.0D;
/* 241:233 */     adjust(this.Right);
/* 242:234 */     adjust(this.LowerText);
/* 243:235 */     this.LowerText.setFont(new Font("SansSerif", 0, (int)(11.0D * this.ratiox + 0.4D)));
/* 244:236 */     adjust(this.Digital);
/* 245:237 */     this.Digital.setFont(new Font("SansSerif", 1, (int)(17.0D * this.ratiox + 0.4D)));
/* 246:238 */     adjust(this.VT100);
/* 247:239 */     this.VT100.setFont(new Font("SansSerif", 1, (int)(17.0D * this.ratiox + 0.4D)));
/* 248:240 */     adjust(this.lamp1);adjust(this.lamp2);adjust(this.lamp3);adjust(this.lamp4);
/* 249:241 */     adjust(this.lamp5);adjust(this.lamp6);adjust(this.lamp7);
/* 250:242 */     adjust(this);
/* 251:243 */     dimback.setSize(panelwidth, panelheight);
/* 252:244 */     return dimback;
/* 253:    */   }
/* 254:    */   
/* 255:    */   private void adjust(JComponent comp)
/* 256:    */   {
/* 257:248 */     Dimension dimold = comp.getPreferredSize();
/* 258:249 */     Dimension dimnew = new Dimension();
/* 259:250 */     dimnew.setSize(dimold.getWidth() * this.ratiox, dimold.getHeight() * this.ratioy);
/* 260:251 */     comp.setPreferredSize(dimnew);
/* 261:252 */     comp.setSize(dimnew);
/* 262:    */   }
/* 263:    */   
/* 264:    */   public void setState(int state)
/* 265:    */   {
/* 266:256 */     this.state = state;
/* 267:257 */     this.lamp1.setState(state & 0x40);
/* 268:258 */     this.lamp2.setState(state & 0x20);
/* 269:259 */     this.lamp3.setState(state & 0x10);
/* 270:260 */     this.lamp4.setState(state & 0x8);
/* 271:261 */     this.lamp5.setState(state & 0x4);
/* 272:262 */     this.lamp6.setState(state & 0x2);
/* 273:263 */     this.lamp7.setState(state & 0x1);
/* 274:    */   }
/* 275:    */   
/* 276:283 */   private int state = -1;
/* 277:    */   double ratiox;
/* 278:    */   double ratioy;
/* 279:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.VT100Lamps
 * JD-Core Version:    0.7.0.1
 */