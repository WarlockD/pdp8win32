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
/*  12:    */ public class StatusLamps
/*  13:    */   extends JPanel
/*  14:    */ {
/*  15:    */   private XLamp statusLamp0;
/*  16:    */   private XLamp statusLamp1;
/*  17:    */   private XLamp statusLamp10;
/*  18:    */   private XLamp statusLamp11;
/*  19:    */   private XLamp statusLamp2;
/*  20:    */   private XLamp statusLamp3;
/*  21:    */   private XLamp statusLamp4;
/*  22:    */   private XLamp statusLamp5;
/*  23:    */   private XLamp statusLamp6;
/*  24:    */   private XLamp statusLamp7;
/*  25:    */   private XLamp statusLamp8;
/*  26:    */   private XLamp statusLamp9;
/*  27:    */   
/*  28:    */   public StatusLamps()
/*  29:    */   {
/*  30: 18 */     initComponents();
/*  31:    */   }
/*  32:    */   
/*  33:    */   private void initComponents()
/*  34:    */   {
/*  35: 29 */     this.statusLamp0 = new XLamp();
/*  36: 30 */     this.statusLamp1 = new XLamp();
/*  37: 31 */     this.statusLamp2 = new XLamp();
/*  38: 32 */     this.statusLamp3 = new XLamp();
/*  39: 33 */     this.statusLamp4 = new XLamp();
/*  40: 34 */     this.statusLamp5 = new XLamp();
/*  41: 35 */     this.statusLamp6 = new XLamp();
/*  42: 36 */     this.statusLamp7 = new XLamp();
/*  43: 37 */     this.statusLamp8 = new XLamp();
/*  44: 38 */     this.statusLamp9 = new XLamp();
/*  45: 39 */     this.statusLamp10 = new XLamp();
/*  46: 40 */     this.statusLamp11 = new XLamp();
/*  47:    */     
/*  48: 42 */     setFont(new Font("Dialog", 0, 8));
/*  49: 43 */     setMaximumSize(new Dimension(312, 12));
/*  50: 44 */     setMinimumSize(new Dimension(156, 6));
/*  51: 45 */     setPreferredSize(new Dimension(312, 12));
/*  52: 46 */     addComponentListener(new ComponentAdapter()
/*  53:    */     {
/*  54:    */       public void componentResized(ComponentEvent evt)
/*  55:    */       {
/*  56: 48 */         StatusLamps.this.formComponentResized(evt);
/*  57:    */       }
/*  58: 50 */     });
/*  59: 51 */     setLayout(new GridBagLayout());
/*  60:    */     
/*  61: 53 */     this.statusLamp0.setBackground(new Color(193, 77, 28));
/*  62: 54 */     this.statusLamp0.setForeground(new Color(0, 0, 0));
/*  63: 55 */     this.statusLamp0.setDoubleBuffered(true);
/*  64: 56 */     this.statusLamp0.setEdge(3);
/*  65: 57 */     this.statusLamp0.setFont(new Font("Dialog", 1, 8));
/*  66: 58 */     this.statusLamp0.setMaximumSize(new Dimension(26, 12));
/*  67: 59 */     this.statusLamp0.setMinimumSize(new Dimension(13, 6));
/*  68: 60 */     this.statusLamp0.setPreferredSize(new Dimension(26, 12));
/*  69: 61 */     this.statusLamp0.setState(false);
/*  70: 62 */     this.statusLamp0.setText("LINK");
/*  71: 63 */     add(this.statusLamp0, new GridBagConstraints());
/*  72:    */     
/*  73: 65 */     this.statusLamp1.setBackground(new Color(193, 77, 28));
/*  74: 66 */     this.statusLamp1.setDoubleBuffered(true);
/*  75: 67 */     this.statusLamp1.setEdge(3);
/*  76: 68 */     this.statusLamp1.setFont(new Font("Dialog", 1, 8));
/*  77: 69 */     this.statusLamp1.setMaximumSize(new Dimension(26, 12));
/*  78: 70 */     this.statusLamp1.setMinimumSize(new Dimension(13, 6));
/*  79: 71 */     this.statusLamp1.setPreferredSize(new Dimension(26, 12));
/*  80: 72 */     this.statusLamp1.setState(false);
/*  81: 73 */     this.statusLamp1.setText("GT");
/*  82: 74 */     add(this.statusLamp1, new GridBagConstraints());
/*  83:    */     
/*  84: 76 */     this.statusLamp2.setBackground(new Color(193, 77, 29));
/*  85: 77 */     this.statusLamp2.setDoubleBuffered(true);
/*  86: 78 */     this.statusLamp2.setEdge(3);
/*  87: 79 */     this.statusLamp2.setFont(new Font("Dialog", 1, 8));
/*  88: 80 */     this.statusLamp2.setMaximumSize(new Dimension(26, 12));
/*  89: 81 */     this.statusLamp2.setMinimumSize(new Dimension(13, 6));
/*  90: 82 */     this.statusLamp2.setPreferredSize(new Dimension(26, 12));
/*  91: 83 */     this.statusLamp2.setState(false);
/*  92: 84 */     this.statusLamp2.setText("INT B");
/*  93: 85 */     add(this.statusLamp2, new GridBagConstraints());
/*  94:    */     
/*  95: 87 */     this.statusLamp3.setBackground(new Color(193, 132, 28));
/*  96: 88 */     this.statusLamp3.setDoubleBuffered(true);
/*  97: 89 */     this.statusLamp3.setEdge(3);
/*  98: 90 */     this.statusLamp3.setFont(new Font("Dialog", 1, 8));
/*  99: 91 */     this.statusLamp3.setMaximumSize(new Dimension(26, 12));
/* 100: 92 */     this.statusLamp3.setMinimumSize(new Dimension(13, 6));
/* 101: 93 */     this.statusLamp3.setPreferredSize(new Dimension(26, 12));
/* 102: 94 */     this.statusLamp3.setState(false);
/* 103: 95 */     this.statusLamp3.setText("NO IN");
/* 104: 96 */     add(this.statusLamp3, new GridBagConstraints());
/* 105:    */     
/* 106: 98 */     this.statusLamp4.setBackground(new Color(193, 132, 28));
/* 107: 99 */     this.statusLamp4.setDoubleBuffered(true);
/* 108:100 */     this.statusLamp4.setEdge(3);
/* 109:101 */     this.statusLamp4.setFont(new Font("Dialog", 1, 8));
/* 110:102 */     this.statusLamp4.setMaximumSize(new Dimension(26, 12));
/* 111:103 */     this.statusLamp4.setMinimumSize(new Dimension(13, 6));
/* 112:104 */     this.statusLamp4.setPreferredSize(new Dimension(26, 12));
/* 113:105 */     this.statusLamp4.setState(false);
/* 114:106 */     this.statusLamp4.setText("ION");
/* 115:107 */     add(this.statusLamp4, new GridBagConstraints());
/* 116:    */     
/* 117:109 */     this.statusLamp5.setBackground(new Color(193, 132, 28));
/* 118:110 */     this.statusLamp5.setDoubleBuffered(true);
/* 119:111 */     this.statusLamp5.setEdge(3);
/* 120:112 */     this.statusLamp5.setFont(new Font("Dialog", 1, 8));
/* 121:113 */     this.statusLamp5.setMaximumSize(new Dimension(26, 12));
/* 122:114 */     this.statusLamp5.setMinimumSize(new Dimension(13, 6));
/* 123:115 */     this.statusLamp5.setPreferredSize(new Dimension(26, 12));
/* 124:116 */     this.statusLamp5.setState(false);
/* 125:117 */     this.statusLamp5.setText("UM");
/* 126:118 */     add(this.statusLamp5, new GridBagConstraints());
/* 127:    */     
/* 128:120 */     this.statusLamp6.setBackground(new Color(193, 77, 28));
/* 129:121 */     this.statusLamp6.setDoubleBuffered(true);
/* 130:122 */     this.statusLamp6.setEdge(3);
/* 131:123 */     this.statusLamp6.setFont(new Font("Dialog", 1, 8));
/* 132:124 */     this.statusLamp6.setMaximumSize(new Dimension(26, 12));
/* 133:125 */     this.statusLamp6.setMinimumSize(new Dimension(13, 6));
/* 134:126 */     this.statusLamp6.setPreferredSize(new Dimension(26, 12));
/* 135:127 */     this.statusLamp6.setState(false);
/* 136:128 */     this.statusLamp6.setText("IF0");
/* 137:129 */     add(this.statusLamp6, new GridBagConstraints());
/* 138:    */     
/* 139:131 */     this.statusLamp7.setBackground(new Color(193, 77, 28));
/* 140:132 */     this.statusLamp7.setDoubleBuffered(true);
/* 141:133 */     this.statusLamp7.setEdge(3);
/* 142:134 */     this.statusLamp7.setFont(new Font("Dialog", 1, 8));
/* 143:135 */     this.statusLamp7.setMaximumSize(new Dimension(26, 12));
/* 144:136 */     this.statusLamp7.setMinimumSize(new Dimension(13, 6));
/* 145:137 */     this.statusLamp7.setPreferredSize(new Dimension(26, 12));
/* 146:138 */     this.statusLamp7.setState(false);
/* 147:139 */     this.statusLamp7.setText("IF1");
/* 148:140 */     add(this.statusLamp7, new GridBagConstraints());
/* 149:    */     
/* 150:142 */     this.statusLamp8.setBackground(new Color(193, 77, 28));
/* 151:143 */     this.statusLamp8.setDoubleBuffered(true);
/* 152:144 */     this.statusLamp8.setEdge(3);
/* 153:145 */     this.statusLamp8.setFont(new Font("Dialog", 1, 8));
/* 154:146 */     this.statusLamp8.setMaximumSize(new Dimension(26, 12));
/* 155:147 */     this.statusLamp8.setMinimumSize(new Dimension(13, 6));
/* 156:148 */     this.statusLamp8.setPreferredSize(new Dimension(26, 12));
/* 157:149 */     this.statusLamp8.setState(false);
/* 158:150 */     this.statusLamp8.setText("IF2");
/* 159:151 */     add(this.statusLamp8, new GridBagConstraints());
/* 160:    */     
/* 161:153 */     this.statusLamp9.setBackground(new Color(193, 132, 28));
/* 162:154 */     this.statusLamp9.setDoubleBuffered(true);
/* 163:155 */     this.statusLamp9.setEdge(3);
/* 164:156 */     this.statusLamp9.setFont(new Font("Dialog", 1, 8));
/* 165:157 */     this.statusLamp9.setMaximumSize(new Dimension(26, 12));
/* 166:158 */     this.statusLamp9.setMinimumSize(new Dimension(13, 6));
/* 167:159 */     this.statusLamp9.setPreferredSize(new Dimension(26, 12));
/* 168:160 */     this.statusLamp9.setState(false);
/* 169:161 */     this.statusLamp9.setText("DF0");
/* 170:162 */     add(this.statusLamp9, new GridBagConstraints());
/* 171:    */     
/* 172:164 */     this.statusLamp10.setBackground(new Color(193, 132, 28));
/* 173:165 */     this.statusLamp10.setDoubleBuffered(true);
/* 174:166 */     this.statusLamp10.setEdge(3);
/* 175:167 */     this.statusLamp10.setFont(new Font("Dialog", 1, 8));
/* 176:168 */     this.statusLamp10.setMaximumSize(new Dimension(26, 12));
/* 177:169 */     this.statusLamp10.setMinimumSize(new Dimension(13, 6));
/* 178:170 */     this.statusLamp10.setPreferredSize(new Dimension(26, 12));
/* 179:171 */     this.statusLamp10.setState(false);
/* 180:172 */     this.statusLamp10.setText("DF1");
/* 181:173 */     add(this.statusLamp10, new GridBagConstraints());
/* 182:    */     
/* 183:175 */     this.statusLamp11.setBackground(new Color(193, 132, 28));
/* 184:176 */     this.statusLamp11.setDoubleBuffered(true);
/* 185:177 */     this.statusLamp11.setEdge(3);
/* 186:178 */     this.statusLamp11.setFont(new Font("Dialog", 1, 8));
/* 187:179 */     this.statusLamp11.setMaximumSize(new Dimension(26, 12));
/* 188:180 */     this.statusLamp11.setMinimumSize(new Dimension(13, 6));
/* 189:181 */     this.statusLamp11.setPreferredSize(new Dimension(26, 12));
/* 190:182 */     this.statusLamp11.setState(false);
/* 191:183 */     this.statusLamp11.setText("DF2");
/* 192:184 */     add(this.statusLamp11, new GridBagConstraints());
/* 193:    */   }
/* 194:    */   
/* 195:    */   private void formComponentResized(ComponentEvent evt)
/* 196:    */   {
/* 197:188 */     if (getWidth() < 300)
/* 198:    */     {
/* 199:189 */       this.statusLamp0.setFont(new Font("Dialog", 1, 4));
/* 200:190 */       this.statusLamp1.setFont(new Font("Dialog", 1, 4));
/* 201:191 */       this.statusLamp2.setFont(new Font("Dialog", 1, 4));
/* 202:192 */       this.statusLamp3.setFont(new Font("Dialog", 1, 4));
/* 203:193 */       this.statusLamp4.setFont(new Font("Dialog", 1, 4));
/* 204:194 */       this.statusLamp5.setFont(new Font("Dialog", 1, 4));
/* 205:195 */       this.statusLamp6.setFont(new Font("Dialog", 1, 4));
/* 206:196 */       this.statusLamp7.setFont(new Font("Dialog", 1, 4));
/* 207:197 */       this.statusLamp8.setFont(new Font("Dialog", 1, 4));
/* 208:198 */       this.statusLamp9.setFont(new Font("Dialog", 1, 4));
/* 209:199 */       this.statusLamp10.setFont(new Font("Dialog", 1, 4));
/* 210:200 */       this.statusLamp11.setFont(new Font("Dialog", 1, 4));
/* 211:    */     }
/* 212:    */     else
/* 213:    */     {
/* 214:202 */       this.statusLamp0.setFont(new Font("Dialog", 1, 8));
/* 215:203 */       this.statusLamp1.setFont(new Font("Dialog", 1, 8));
/* 216:204 */       this.statusLamp2.setFont(new Font("Dialog", 1, 8));
/* 217:205 */       this.statusLamp3.setFont(new Font("Dialog", 1, 8));
/* 218:206 */       this.statusLamp4.setFont(new Font("Dialog", 1, 8));
/* 219:207 */       this.statusLamp5.setFont(new Font("Dialog", 1, 8));
/* 220:208 */       this.statusLamp6.setFont(new Font("Dialog", 1, 8));
/* 221:209 */       this.statusLamp7.setFont(new Font("Dialog", 1, 8));
/* 222:210 */       this.statusLamp8.setFont(new Font("Dialog", 1, 8));
/* 223:211 */       this.statusLamp9.setFont(new Font("Dialog", 1, 8));
/* 224:212 */       this.statusLamp10.setFont(new Font("Dialog", 1, 8));
/* 225:213 */       this.statusLamp11.setFont(new Font("Dialog", 1, 8));
/* 226:    */     }
/* 227:    */   }
/* 228:    */   
/* 229:    */   public void setState(int state)
/* 230:    */   {
/* 231:223 */     if (this.state != state)
/* 232:    */     {
/* 233:224 */       this.state = state;
/* 234:225 */       this.statusLamp0.setState(state & 0x800);
/* 235:226 */       this.statusLamp1.setState(state & 0x400);
/* 236:227 */       this.statusLamp2.setState(state & 0x200);
/* 237:228 */       this.statusLamp3.setState(state & 0x100);
/* 238:229 */       this.statusLamp4.setState(state & 0x80);
/* 239:230 */       this.statusLamp5.setState(state & 0x40);
/* 240:231 */       this.statusLamp6.setState(state & 0x20);
/* 241:232 */       this.statusLamp7.setState(state & 0x10);
/* 242:233 */       this.statusLamp8.setState(state & 0x8);
/* 243:234 */       this.statusLamp9.setState(state & 0x4);
/* 244:235 */       this.statusLamp10.setState(state & 0x2);
/* 245:236 */       this.statusLamp11.setState(state & 0x1);
/* 246:    */     }
/* 247:    */   }
/* 248:    */   
/* 249:260 */   private int state = -1;
/* 250:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.StatusLamps
 * JD-Core Version:    0.7.0.1
 */