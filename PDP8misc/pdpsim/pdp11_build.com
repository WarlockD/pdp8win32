$ cc/debug/optim=noinline pdp11_cpu
$ cc/debug/optim=noinline pdp11_fp
$ cc/debug/optim=noinline pdp11_sys
$ cc/debug/optim=noinline pdp11_stddev
$ cc/debug/optim=noinline pdp11_lp
$ cc/debug/optim=noinline pdp11_rk
$ cc/debug/optim=noinline pdp11_rl
$ cc/debug/optim=noinline pdp11_rx
$ cc/debug/optim=noinline scp,scp_tty
$ link/debug/exec=pdp11 pdp11_cpu,pdp11_fp,pdp11_sys,pdp11_stddev,pdp11_lp,-
  pdp11_rl,pdp11_rx,pdp11_rk,scp,scp_tty,sys$library:vaxcrtl/lib
