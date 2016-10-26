/*  1:   */ package Logic;
/*  2:   */ 
/*  3:   */ public class VirQueue
/*  4:   */ {
/*  5:   */   VirTimer firstTimer;
/*  6:   */   BusRegMem data;
/*  7:   */   
/*  8:   */   public VirQueue(BusRegMem data)
/*  9:   */   {
/* 10:17 */     this.data = data;
/* 11:   */   }
/* 12:   */   
/* 13:   */   public void postExpiredTimers()
/* 14:   */   {
/* 15:   */     long l1;
/* 16:   */     do
/* 17:   */     {
/* 18:23 */       VirTimer timer = this.firstTimer;
/* 19:24 */       if (timer == null) {
/* 20:25 */         return;
/* 21:   */       }
/* 22:26 */       long l = this.data.pdp8time;
/* 23:27 */       l1 = timer.expirationTime - l;
/* 24:28 */       if (l1 <= 0L)
/* 25:   */       {
/* 26:29 */         removeTimer(timer);
/* 27:30 */         timer.post();
/* 28:31 */         if (timer.isRepeats()) {
/* 29:32 */           addTimer(timer, timer.getDelay());
/* 30:   */         }
/* 31:   */       }
/* 32:34 */     } while (l1 <= 0L);
/* 33:   */   }
/* 34:   */   
/* 35:   */   void removeTimer(VirTimer timer)
/* 36:   */   {
/* 37:39 */     if (!timer.running) {
/* 38:40 */       return;
/* 39:   */     }
/* 40:41 */     VirTimer timer1 = null;
/* 41:42 */     VirTimer timer2 = this.firstTimer;
/* 42:43 */     boolean flag = false;
/* 43:45 */     while (timer2 != null)
/* 44:   */     {
/* 45:47 */       if (timer2 == timer)
/* 46:   */       {
/* 47:48 */         flag = true;
/* 48:49 */         break;
/* 49:   */       }
/* 50:51 */       timer1 = timer2;
/* 51:52 */       timer2 = timer2.nextTimer;
/* 52:   */     }
/* 53:54 */     if (!flag) {
/* 54:55 */       return;
/* 55:   */     }
/* 56:56 */     if (timer1 == null) {
/* 57:57 */       this.firstTimer = timer.nextTimer;
/* 58:   */     } else {
/* 59:59 */       timer1.nextTimer = timer.nextTimer;
/* 60:   */     }
/* 61:60 */     timer.expirationTime = 0L;
/* 62:61 */     timer.nextTimer = null;
/* 63:62 */     timer.running = false;
/* 64:   */   }
/* 65:   */   
/* 66:   */   boolean containsTimer(VirTimer timer)
/* 67:   */   {
/* 68:66 */     return timer.running;
/* 69:   */   }
/* 70:   */   
/* 71:   */   void addTimer(VirTimer timer, long l)
/* 72:   */   {
/* 73:71 */     long l1 = this.data.pdp8time + l;
/* 74:72 */     if (timer.running) {
/* 75:73 */       return;
/* 76:   */     }
/* 77:74 */     VirTimer timer1 = null;
/* 78:76 */     for (VirTimer timer2 = this.firstTimer; (timer2 != null) && (timer2.expirationTime <= l1); timer2 = timer2.nextTimer) {
/* 79:77 */       timer1 = timer2;
/* 80:   */     }
/* 81:79 */     if (timer1 == null) {
/* 82:80 */       this.firstTimer = timer;
/* 83:   */     } else {
/* 84:82 */       timer1.nextTimer = timer;
/* 85:   */     }
/* 86:83 */     timer.expirationTime = l1;
/* 87:84 */     timer.nextTimer = timer2;
/* 88:85 */     timer.running = true;
/* 89:   */   }
/* 90:   */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.VirQueue
 * JD-Core Version:    0.7.0.1
 */