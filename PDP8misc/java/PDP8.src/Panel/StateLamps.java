/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.GridBagConstraints;
/*   7:    */ import java.awt.GridBagLayout;
/*   8:    */ import java.awt.event.ComponentAdapter;
/*   9:    */ import java.awt.event.ComponentEvent;
/*  10:    */ import javax.swing.JPanel;
/*  11:    */ 
/*  12:    */ public class StateLamps
/*  13:    */   extends JPanel
/*  14:    */ {
/*  15:    */   private XLamp stateLamp0;
/*  16:    */   private XLamp stateLamp1;
/*  17:    */   private XLamp stateLamp10;
/*  18:    */   private XLamp stateLamp11;
/*  19:    */   private XLamp stateLamp2;
/*  20:    */   private XLamp stateLamp3;
/*  21:    */   private XLamp stateLamp4;
/*  22:    */   private XLamp stateLamp5;
/*  23:    */   private XLamp stateLamp6;
/*  24:    */   private XLamp stateLamp7;
/*  25:    */   private XLamp stateLamp8;
/*  26:    */   private XLamp stateLamp9;
/*  27:    */   
/*  28:    */   public StateLamps()
/*  29:    */   {
/*  30: 16 */     initComponents();
/*  31:    */   }
/*  32:    */   
/*  33:    */   private void initComponents()
/*  34:    */   {
/*  35: 27 */     this.stateLamp0 = new XLamp();
/*  36: 28 */     this.stateLamp1 = new XLamp();
/*  37: 29 */     this.stateLamp2 = new XLamp();
/*  38: 30 */     this.stateLamp3 = new XLamp();
/*  39: 31 */     this.stateLamp4 = new XLamp();
/*  40: 32 */     this.stateLamp5 = new XLamp();
/*  41: 33 */     this.stateLamp6 = new XLamp();
/*  42: 34 */     this.stateLamp7 = new XLamp();
/*  43: 35 */     this.stateLamp8 = new XLamp();
/*  44: 36 */     this.stateLamp9 = new XLamp();
/*  45: 37 */     this.stateLamp10 = new XLamp();
/*  46: 38 */     this.stateLamp11 = new XLamp();
/*  47:    */     
/*  48: 40 */     setFont(new Font("Dialog", 0, 8));
/*  49: 41 */     setMaximumSize(new Dimension(312, 12));
/*  50: 42 */     setMinimumSize(new Dimension(156, 6));
/*  51: 43 */     setPreferredSize(new Dimension(312, 12));
/*  52: 44 */     addComponentListener(new ComponentAdapter()
/*  53:    */     {
/*  54:    */       public void componentResized(ComponentEvent evt)
/*  55:    */       {
/*  56: 46 */         StateLamps.this.formComponentResized(evt);
/*  57:    */       }
/*  58: 48 */     });
/*  59: 49 */     setLayout(new GridBagLayout());
/*  60:    */     
/*  61: 51 */     this.stateLamp0.setBackground(new Color(193, 77, 28));
/*  62: 52 */     this.stateLamp0.setForeground(new Color(0, 0, 0));
/*  63: 53 */     this.stateLamp0.setDoubleBuffered(true);
/*  64: 54 */     this.stateLamp0.setEdge(3);
/*  65: 55 */     this.stateLamp0.setFont(new Font("Dialog", 1, 8));
/*  66: 56 */     this.stateLamp0.setMaximumSize(new Dimension(26, 12));
/*  67: 57 */     this.stateLamp0.setMinimumSize(new Dimension(13, 6));
/*  68: 58 */     this.stateLamp0.setPreferredSize(new Dimension(26, 12));
/*  69: 59 */     this.stateLamp0.setState(false);
/*  70: 60 */     this.stateLamp0.setText("F");
/*  71: 61 */     add(this.stateLamp0, new GridBagConstraints());
/*  72:    */     
/*  73: 63 */     this.stateLamp1.setBackground(new Color(193, 77, 28));
/*  74: 64 */     this.stateLamp1.setDoubleBuffered(true);
/*  75: 65 */     this.stateLamp1.setEdge(3);
/*  76: 66 */     this.stateLamp1.setFont(new Font("Dialog", 1, 8));
/*  77: 67 */     this.stateLamp1.setMaximumSize(new Dimension(26, 12));
/*  78: 68 */     this.stateLamp1.setMinimumSize(new Dimension(13, 6));
/*  79: 69 */     this.stateLamp1.setPreferredSize(new Dimension(26, 12));
/*  80: 70 */     this.stateLamp1.setState(false);
/*  81: 71 */     this.stateLamp1.setText("D");
/*  82: 72 */     add(this.stateLamp1, new GridBagConstraints());
/*  83:    */     
/*  84: 74 */     this.stateLamp2.setBackground(new Color(193, 77, 29));
/*  85: 75 */     this.stateLamp2.setDoubleBuffered(true);
/*  86: 76 */     this.stateLamp2.setEdge(3);
/*  87: 77 */     this.stateLamp2.setFont(new Font("Dialog", 1, 8));
/*  88: 78 */     this.stateLamp2.setMaximumSize(new Dimension(26, 12));
/*  89: 79 */     this.stateLamp2.setMinimumSize(new Dimension(13, 6));
/*  90: 80 */     this.stateLamp2.setPreferredSize(new Dimension(26, 12));
/*  91: 81 */     this.stateLamp2.setState(false);
/*  92: 82 */     this.stateLamp2.setText("E");
/*  93: 83 */     add(this.stateLamp2, new GridBagConstraints());
/*  94:    */     
/*  95: 85 */     this.stateLamp3.setBackground(new Color(193, 132, 28));
/*  96: 86 */     this.stateLamp3.setDoubleBuffered(true);
/*  97: 87 */     this.stateLamp3.setEdge(3);
/*  98: 88 */     this.stateLamp3.setFont(new Font("Dialog", 1, 8));
/*  99: 89 */     this.stateLamp3.setMaximumSize(new Dimension(26, 12));
/* 100: 90 */     this.stateLamp3.setMinimumSize(new Dimension(13, 6));
/* 101: 91 */     this.stateLamp3.setPreferredSize(new Dimension(26, 12));
/* 102: 92 */     this.stateLamp3.setState(false);
/* 103: 93 */     this.stateLamp3.setText("IR0");
/* 104: 94 */     add(this.stateLamp3, new GridBagConstraints());
/* 105:    */     
/* 106: 96 */     this.stateLamp4.setBackground(new Color(193, 132, 28));
/* 107: 97 */     this.stateLamp4.setDoubleBuffered(true);
/* 108: 98 */     this.stateLamp4.setEdge(3);
/* 109: 99 */     this.stateLamp4.setFont(new Font("Dialog", 1, 8));
/* 110:100 */     this.stateLamp4.setMaximumSize(new Dimension(26, 12));
/* 111:101 */     this.stateLamp4.setMinimumSize(new Dimension(13, 6));
/* 112:102 */     this.stateLamp4.setPreferredSize(new Dimension(26, 12));
/* 113:103 */     this.stateLamp4.setState(false);
/* 114:104 */     this.stateLamp4.setText("IR1");
/* 115:105 */     add(this.stateLamp4, new GridBagConstraints());
/* 116:    */     
/* 117:107 */     this.stateLamp5.setBackground(new Color(193, 132, 28));
/* 118:108 */     this.stateLamp5.setDoubleBuffered(true);
/* 119:109 */     this.stateLamp5.setEdge(3);
/* 120:110 */     this.stateLamp5.setFont(new Font("Dialog", 1, 8));
/* 121:111 */     this.stateLamp5.setMaximumSize(new Dimension(26, 12));
/* 122:112 */     this.stateLamp5.setMinimumSize(new Dimension(13, 6));
/* 123:113 */     this.stateLamp5.setPreferredSize(new Dimension(26, 12));
/* 124:114 */     this.stateLamp5.setState(false);
/* 125:115 */     this.stateLamp5.setText("IR2");
/* 126:116 */     add(this.stateLamp5, new GridBagConstraints());
/* 127:    */     
/* 128:118 */     this.stateLamp6.setBackground(new Color(193, 77, 28));
/* 129:119 */     this.stateLamp6.setDoubleBuffered(true);
/* 130:120 */     this.stateLamp6.setEdge(3);
/* 131:121 */     this.stateLamp6.setFont(new Font("Dialog", 1, 8));
/* 132:122 */     this.stateLamp6.setMaximumSize(new Dimension(26, 12));
/* 133:123 */     this.stateLamp6.setMinimumSize(new Dimension(13, 6));
/* 134:124 */     this.stateLamp6.setPreferredSize(new Dimension(26, 12));
/* 135:125 */     this.stateLamp6.setState(false);
/* 136:126 */     this.stateLamp6.setText("MDD");
/* 137:127 */     add(this.stateLamp6, new GridBagConstraints());
/* 138:    */     
/* 139:129 */     this.stateLamp7.setBackground(new Color(193, 77, 28));
/* 140:130 */     this.stateLamp7.setDoubleBuffered(true);
/* 141:131 */     this.stateLamp7.setEdge(3);
/* 142:132 */     this.stateLamp7.setFont(new Font("Dialog", 1, 8));
/* 143:133 */     this.stateLamp7.setMaximumSize(new Dimension(26, 12));
/* 144:134 */     this.stateLamp7.setMinimumSize(new Dimension(13, 6));
/* 145:135 */     this.stateLamp7.setPreferredSize(new Dimension(26, 12));
/* 146:136 */     this.stateLamp7.setState(false);
/* 147:137 */     this.stateLamp7.setText("BDC");
/* 148:138 */     add(this.stateLamp7, new GridBagConstraints());
/* 149:    */     
/* 150:140 */     this.stateLamp8.setBackground(new Color(193, 77, 28));
/* 151:141 */     this.stateLamp8.setDoubleBuffered(true);
/* 152:142 */     this.stateLamp8.setEdge(3);
/* 153:143 */     this.stateLamp8.setFont(new Font("Dialog", 1, 8));
/* 154:144 */     this.stateLamp8.setMaximumSize(new Dimension(26, 12));
/* 155:145 */     this.stateLamp8.setMinimumSize(new Dimension(13, 6));
/* 156:146 */     this.stateLamp8.setPreferredSize(new Dimension(26, 12));
/* 157:147 */     this.stateLamp8.setState(false);
/* 158:148 */     this.stateLamp8.setText("SW");
/* 159:149 */     add(this.stateLamp8, new GridBagConstraints());
/* 160:    */     
/* 161:151 */     this.stateLamp9.setBackground(new Color(193, 132, 28));
/* 162:152 */     this.stateLamp9.setDoubleBuffered(true);
/* 163:153 */     this.stateLamp9.setEdge(3);
/* 164:154 */     this.stateLamp9.setFont(new Font("Dialog", 1, 8));
/* 165:155 */     this.stateLamp9.setMaximumSize(new Dimension(26, 12));
/* 166:156 */     this.stateLamp9.setMinimumSize(new Dimension(13, 6));
/* 167:157 */     this.stateLamp9.setPreferredSize(new Dimension(26, 12));
/* 168:158 */     this.stateLamp9.setState(false);
/* 169:159 */     this.stateLamp9.setText("PAU");
/* 170:160 */     add(this.stateLamp9, new GridBagConstraints());
/* 171:    */     
/* 172:162 */     this.stateLamp10.setBackground(new Color(193, 132, 28));
/* 173:163 */     this.stateLamp10.setDoubleBuffered(true);
/* 174:164 */     this.stateLamp10.setEdge(3);
/* 175:165 */     this.stateLamp10.setFont(new Font("Dialog", 1, 8));
/* 176:166 */     this.stateLamp10.setMaximumSize(new Dimension(26, 12));
/* 177:167 */     this.stateLamp10.setMinimumSize(new Dimension(13, 6));
/* 178:168 */     this.stateLamp10.setPreferredSize(new Dimension(26, 12));
/* 179:169 */     this.stateLamp10.setState(false);
/* 180:170 */     this.stateLamp10.setText("BRKP");
/* 181:171 */     add(this.stateLamp10, new GridBagConstraints());
/* 182:    */     
/* 183:173 */     this.stateLamp11.setBackground(new Color(193, 132, 28));
/* 184:174 */     this.stateLamp11.setDoubleBuffered(true);
/* 185:175 */     this.stateLamp11.setEdge(3);
/* 186:176 */     this.stateLamp11.setFont(new Font("Dialog", 1, 8));
/* 187:177 */     this.stateLamp11.setMaximumSize(new Dimension(26, 12));
/* 188:178 */     this.stateLamp11.setMinimumSize(new Dimension(13, 6));
/* 189:179 */     this.stateLamp11.setPreferredSize(new Dimension(26, 12));
/* 190:180 */     this.stateLamp11.setState(false);
/* 191:181 */     this.stateLamp11.setText("BRKC");
/* 192:182 */     add(this.stateLamp11, new GridBagConstraints());
/* 193:    */   }
/* 194:    */   
/* 195:    */   private void formComponentResized(ComponentEvent evt)
/* 196:    */   {
/* 197:186 */     if (getWidth() < 300)
/* 198:    */     {
/* 199:187 */       this.stateLamp0.setFont(new Font("Dialog", 1, 4));
/* 200:188 */       this.stateLamp1.setFont(new Font("Dialog", 1, 4));
/* 201:189 */       this.stateLamp2.setFont(new Font("Dialog", 1, 4));
/* 202:190 */       this.stateLamp3.setFont(new Font("Dialog", 1, 4));
/* 203:191 */       this.stateLamp4.setFont(new Font("Dialog", 1, 4));
/* 204:192 */       this.stateLamp5.setFont(new Font("Dialog", 1, 4));
/* 205:193 */       this.stateLamp6.setFont(new Font("Dialog", 1, 4));
/* 206:194 */       this.stateLamp7.setFont(new Font("Dialog", 1, 4));
/* 207:195 */       this.stateLamp8.setFont(new Font("Dialog", 1, 4));
/* 208:196 */       this.stateLamp9.setFont(new Font("Dialog", 1, 4));
/* 209:197 */       this.stateLamp10.setFont(new Font("Dialog", 1, 4));
/* 210:198 */       this.stateLamp11.setFont(new Font("Dialog", 1, 4));
/* 211:    */     }
/* 212:    */     else
/* 213:    */     {
/* 214:200 */       this.stateLamp0.setFont(new Font("Dialog", 1, 8));
/* 215:201 */       this.stateLamp1.setFont(new Font("Dialog", 1, 8));
/* 216:202 */       this.stateLamp2.setFont(new Font("Dialog", 1, 8));
/* 217:203 */       this.stateLamp3.setFont(new Font("Dialog", 1, 8));
/* 218:204 */       this.stateLamp4.setFont(new Font("Dialog", 1, 8));
/* 219:205 */       this.stateLamp5.setFont(new Font("Dialog", 1, 8));
/* 220:206 */       this.stateLamp6.setFont(new Font("Dialog", 1, 8));
/* 221:207 */       this.stateLamp7.setFont(new Font("Dialog", 1, 8));
/* 222:208 */       this.stateLamp8.setFont(new Font("Dialog", 1, 8));
/* 223:209 */       this.stateLamp9.setFont(new Font("Dialog", 1, 8));
/* 224:210 */       this.stateLamp10.setFont(new Font("Dialog", 1, 8));
/* 225:211 */       this.stateLamp11.setFont(new Font("Dialog", 1, 8));
/* 226:    */     }
/* 227:    */   }
/* 228:    */   
/* 229:    */   public void setState(int state)
/* 230:    */   {
/* 231:221 */     if (this.state != state)
/* 232:    */     {
/* 233:222 */       this.state = state;
/* 234:223 */       this.stateLamp0.setState(state & 0x800);
/* 235:224 */       this.stateLamp1.setState(state & 0x400);
/* 236:225 */       this.stateLamp2.setState(state & 0x200);
/* 237:226 */       this.stateLamp3.setState(state & 0x100);
/* 238:227 */       this.stateLamp4.setState(state & 0x80);
/* 239:228 */       this.stateLamp5.setState(state & 0x40);
/* 240:229 */       this.stateLamp6.setState(state & 0x20);
/* 241:230 */       this.stateLamp7.setState(state & 0x10);
/* 242:231 */       this.stateLamp8.setState(state & 0x8);
/* 243:232 */       this.stateLamp9.setState(state & 0x4);
/* 244:233 */       this.stateLamp10.setState(state & 0x2);
/* 245:234 */       this.stateLamp11.setState(state & 0x1);
/* 246:    */     }
/* 247:    */   }
/* 248:    */   
/* 249:254 */   private int state = -1;
/* 250:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.StateLamps
 * JD-Core Version:    0.7.0.1
 */