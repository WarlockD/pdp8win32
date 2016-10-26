/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.GridBagConstraints;
/*   7:    */ import java.awt.GridBagLayout;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ 
/*  10:    */ public class DataLamps
/*  11:    */   extends JPanel
/*  12:    */ {
/*  13:    */   private XLamp dataLamp0;
/*  14:    */   private XLamp dataLamp1;
/*  15:    */   private XLamp dataLamp10;
/*  16:    */   private XLamp dataLamp11;
/*  17:    */   private XLamp dataLamp2;
/*  18:    */   private XLamp dataLamp3;
/*  19:    */   private XLamp dataLamp4;
/*  20:    */   private XLamp dataLamp5;
/*  21:    */   private XLamp dataLamp6;
/*  22:    */   private XLamp dataLamp7;
/*  23:    */   private XLamp dataLamp8;
/*  24:    */   private XLamp dataLamp9;
/*  25:    */   
/*  26:    */   public DataLamps()
/*  27:    */   {
/*  28: 18 */     initComponents();
/*  29:    */   }
/*  30:    */   
/*  31:    */   private void initComponents()
/*  32:    */   {
/*  33: 29 */     this.dataLamp0 = new XLamp();
/*  34: 30 */     this.dataLamp1 = new XLamp();
/*  35: 31 */     this.dataLamp2 = new XLamp();
/*  36: 32 */     this.dataLamp3 = new XLamp();
/*  37: 33 */     this.dataLamp4 = new XLamp();
/*  38: 34 */     this.dataLamp5 = new XLamp();
/*  39: 35 */     this.dataLamp6 = new XLamp();
/*  40: 36 */     this.dataLamp7 = new XLamp();
/*  41: 37 */     this.dataLamp8 = new XLamp();
/*  42: 38 */     this.dataLamp9 = new XLamp();
/*  43: 39 */     this.dataLamp10 = new XLamp();
/*  44: 40 */     this.dataLamp11 = new XLamp();
/*  45:    */     
/*  46: 42 */     setFont(new Font("Dialog", 0, 8));
/*  47: 43 */     setMaximumSize(new Dimension(312, 12));
/*  48: 44 */     setMinimumSize(new Dimension(156, 6));
/*  49: 45 */     setPreferredSize(new Dimension(312, 12));
/*  50: 46 */     setLayout(new GridBagLayout());
/*  51:    */     
/*  52: 48 */     this.dataLamp0.setBackground(new Color(193, 77, 28));
/*  53: 49 */     this.dataLamp0.setForeground(new Color(0, 0, 0));
/*  54: 50 */     this.dataLamp0.setDoubleBuffered(true);
/*  55: 51 */     this.dataLamp0.setEdge(1);
/*  56: 52 */     this.dataLamp0.setFont(new Font("Dialog", 1, 8));
/*  57: 53 */     this.dataLamp0.setMaximumSize(new Dimension(26, 12));
/*  58: 54 */     this.dataLamp0.setMinimumSize(new Dimension(13, 6));
/*  59: 55 */     this.dataLamp0.setPreferredSize(new Dimension(26, 12));
/*  60: 56 */     this.dataLamp0.setState(false);
/*  61: 57 */     this.dataLamp0.setText("");
/*  62: 58 */     add(this.dataLamp0, new GridBagConstraints());
/*  63:    */     
/*  64: 60 */     this.dataLamp1.setBackground(new Color(193, 77, 28));
/*  65: 61 */     this.dataLamp1.setDoubleBuffered(true);
/*  66: 62 */     this.dataLamp1.setEdge(1);
/*  67: 63 */     this.dataLamp1.setFont(new Font("Dialog", 1, 8));
/*  68: 64 */     this.dataLamp1.setMaximumSize(new Dimension(26, 12));
/*  69: 65 */     this.dataLamp1.setMinimumSize(new Dimension(13, 6));
/*  70: 66 */     this.dataLamp1.setPreferredSize(new Dimension(26, 12));
/*  71: 67 */     this.dataLamp1.setState(false);
/*  72: 68 */     this.dataLamp1.setText("");
/*  73: 69 */     add(this.dataLamp1, new GridBagConstraints());
/*  74:    */     
/*  75: 71 */     this.dataLamp2.setBackground(new Color(193, 77, 29));
/*  76: 72 */     this.dataLamp2.setDoubleBuffered(true);
/*  77: 73 */     this.dataLamp2.setEdge(1);
/*  78: 74 */     this.dataLamp2.setFont(new Font("Dialog", 1, 8));
/*  79: 75 */     this.dataLamp2.setMaximumSize(new Dimension(26, 12));
/*  80: 76 */     this.dataLamp2.setMinimumSize(new Dimension(13, 6));
/*  81: 77 */     this.dataLamp2.setPreferredSize(new Dimension(26, 12));
/*  82: 78 */     this.dataLamp2.setState(false);
/*  83: 79 */     this.dataLamp2.setText("");
/*  84: 80 */     add(this.dataLamp2, new GridBagConstraints());
/*  85:    */     
/*  86: 82 */     this.dataLamp3.setBackground(new Color(193, 132, 28));
/*  87: 83 */     this.dataLamp3.setDoubleBuffered(true);
/*  88: 84 */     this.dataLamp3.setEdge(1);
/*  89: 85 */     this.dataLamp3.setFont(new Font("Dialog", 1, 8));
/*  90: 86 */     this.dataLamp3.setMaximumSize(new Dimension(26, 12));
/*  91: 87 */     this.dataLamp3.setMinimumSize(new Dimension(13, 6));
/*  92: 88 */     this.dataLamp3.setPreferredSize(new Dimension(26, 12));
/*  93: 89 */     this.dataLamp3.setState(false);
/*  94: 90 */     this.dataLamp3.setText("");
/*  95: 91 */     add(this.dataLamp3, new GridBagConstraints());
/*  96:    */     
/*  97: 93 */     this.dataLamp4.setBackground(new Color(193, 132, 28));
/*  98: 94 */     this.dataLamp4.setDoubleBuffered(true);
/*  99: 95 */     this.dataLamp4.setEdge(1);
/* 100: 96 */     this.dataLamp4.setFont(new Font("Dialog", 1, 8));
/* 101: 97 */     this.dataLamp4.setMaximumSize(new Dimension(26, 12));
/* 102: 98 */     this.dataLamp4.setMinimumSize(new Dimension(13, 6));
/* 103: 99 */     this.dataLamp4.setPreferredSize(new Dimension(26, 12));
/* 104:100 */     this.dataLamp4.setState(false);
/* 105:101 */     this.dataLamp4.setText("");
/* 106:102 */     add(this.dataLamp4, new GridBagConstraints());
/* 107:    */     
/* 108:104 */     this.dataLamp5.setBackground(new Color(193, 132, 28));
/* 109:105 */     this.dataLamp5.setDoubleBuffered(true);
/* 110:106 */     this.dataLamp5.setEdge(1);
/* 111:107 */     this.dataLamp5.setFont(new Font("Dialog", 1, 8));
/* 112:108 */     this.dataLamp5.setMaximumSize(new Dimension(26, 12));
/* 113:109 */     this.dataLamp5.setMinimumSize(new Dimension(13, 6));
/* 114:110 */     this.dataLamp5.setPreferredSize(new Dimension(26, 12));
/* 115:111 */     this.dataLamp5.setState(false);
/* 116:112 */     this.dataLamp5.setText("");
/* 117:113 */     add(this.dataLamp5, new GridBagConstraints());
/* 118:    */     
/* 119:115 */     this.dataLamp6.setBackground(new Color(193, 77, 28));
/* 120:116 */     this.dataLamp6.setDoubleBuffered(true);
/* 121:117 */     this.dataLamp6.setEdge(1);
/* 122:118 */     this.dataLamp6.setFont(new Font("Dialog", 1, 8));
/* 123:119 */     this.dataLamp6.setMaximumSize(new Dimension(26, 12));
/* 124:120 */     this.dataLamp6.setMinimumSize(new Dimension(13, 6));
/* 125:121 */     this.dataLamp6.setPreferredSize(new Dimension(26, 12));
/* 126:122 */     this.dataLamp6.setState(false);
/* 127:123 */     this.dataLamp6.setText("");
/* 128:124 */     add(this.dataLamp6, new GridBagConstraints());
/* 129:    */     
/* 130:126 */     this.dataLamp7.setBackground(new Color(193, 77, 28));
/* 131:127 */     this.dataLamp7.setDoubleBuffered(true);
/* 132:128 */     this.dataLamp7.setEdge(1);
/* 133:129 */     this.dataLamp7.setFont(new Font("Dialog", 1, 8));
/* 134:130 */     this.dataLamp7.setMaximumSize(new Dimension(26, 12));
/* 135:131 */     this.dataLamp7.setMinimumSize(new Dimension(13, 6));
/* 136:132 */     this.dataLamp7.setPreferredSize(new Dimension(26, 12));
/* 137:133 */     this.dataLamp7.setState(false);
/* 138:134 */     this.dataLamp7.setText("");
/* 139:135 */     add(this.dataLamp7, new GridBagConstraints());
/* 140:    */     
/* 141:137 */     this.dataLamp8.setBackground(new Color(193, 77, 28));
/* 142:138 */     this.dataLamp8.setDoubleBuffered(true);
/* 143:139 */     this.dataLamp8.setEdge(1);
/* 144:140 */     this.dataLamp8.setFont(new Font("Dialog", 1, 8));
/* 145:141 */     this.dataLamp8.setMaximumSize(new Dimension(26, 12));
/* 146:142 */     this.dataLamp8.setMinimumSize(new Dimension(13, 6));
/* 147:143 */     this.dataLamp8.setPreferredSize(new Dimension(26, 12));
/* 148:144 */     this.dataLamp8.setState(false);
/* 149:145 */     this.dataLamp8.setText("");
/* 150:146 */     add(this.dataLamp8, new GridBagConstraints());
/* 151:    */     
/* 152:148 */     this.dataLamp9.setBackground(new Color(193, 132, 28));
/* 153:149 */     this.dataLamp9.setDoubleBuffered(true);
/* 154:150 */     this.dataLamp9.setEdge(1);
/* 155:151 */     this.dataLamp9.setFont(new Font("Dialog", 1, 8));
/* 156:152 */     this.dataLamp9.setMaximumSize(new Dimension(26, 12));
/* 157:153 */     this.dataLamp9.setMinimumSize(new Dimension(13, 6));
/* 158:154 */     this.dataLamp9.setPreferredSize(new Dimension(26, 12));
/* 159:155 */     this.dataLamp9.setState(false);
/* 160:156 */     this.dataLamp9.setText("");
/* 161:157 */     add(this.dataLamp9, new GridBagConstraints());
/* 162:    */     
/* 163:159 */     this.dataLamp10.setBackground(new Color(193, 132, 28));
/* 164:160 */     this.dataLamp10.setDoubleBuffered(true);
/* 165:161 */     this.dataLamp10.setEdge(1);
/* 166:162 */     this.dataLamp10.setFont(new Font("Dialog", 1, 8));
/* 167:163 */     this.dataLamp10.setMaximumSize(new Dimension(26, 12));
/* 168:164 */     this.dataLamp10.setMinimumSize(new Dimension(13, 6));
/* 169:165 */     this.dataLamp10.setPreferredSize(new Dimension(26, 12));
/* 170:166 */     this.dataLamp10.setState(false);
/* 171:167 */     this.dataLamp10.setText("");
/* 172:168 */     add(this.dataLamp10, new GridBagConstraints());
/* 173:    */     
/* 174:170 */     this.dataLamp11.setBackground(new Color(193, 132, 28));
/* 175:171 */     this.dataLamp11.setDoubleBuffered(true);
/* 176:172 */     this.dataLamp11.setEdge(1);
/* 177:173 */     this.dataLamp11.setFont(new Font("Dialog", 1, 8));
/* 178:174 */     this.dataLamp11.setMaximumSize(new Dimension(26, 12));
/* 179:175 */     this.dataLamp11.setMinimumSize(new Dimension(13, 6));
/* 180:176 */     this.dataLamp11.setPreferredSize(new Dimension(26, 12));
/* 181:177 */     this.dataLamp11.setState(false);
/* 182:178 */     this.dataLamp11.setText("");
/* 183:179 */     add(this.dataLamp11, new GridBagConstraints());
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void setState(int state)
/* 187:    */   {
/* 188:187 */     if (this.state != state)
/* 189:    */     {
/* 190:188 */       this.state = state;
/* 191:189 */       this.dataLamp0.setState(state & 0x800);
/* 192:190 */       this.dataLamp1.setState(state & 0x400);
/* 193:191 */       this.dataLamp2.setState(state & 0x200);
/* 194:192 */       this.dataLamp3.setState(state & 0x100);
/* 195:193 */       this.dataLamp4.setState(state & 0x80);
/* 196:194 */       this.dataLamp5.setState(state & 0x40);
/* 197:195 */       this.dataLamp6.setState(state & 0x20);
/* 198:196 */       this.dataLamp7.setState(state & 0x10);
/* 199:197 */       this.dataLamp8.setState(state & 0x8);
/* 200:198 */       this.dataLamp9.setState(state & 0x4);
/* 201:199 */       this.dataLamp10.setState(state & 0x2);
/* 202:200 */       this.dataLamp11.setState(state & 0x1);
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:224 */   private int state = -1;
/* 207:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.DataLamps
 * JD-Core Version:    0.7.0.1
 */