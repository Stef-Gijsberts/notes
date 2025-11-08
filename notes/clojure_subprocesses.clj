; # Subprocesses in Clojure

; In this document we explore starting subprocesses and interacting with them.
;
; For this we use `clojure.java.process`, introduced in clojure version 1.12.

(ns clojure-subprocesses
  (:require
   [clojure.java.process :as p]
   [clojure.java.io :as io]))

;
; # A simple case with `ls`
;
; We will first look at `ls`. It is simple and does not take any inputs.

; ## Starting the process
;
; We use `clojure.java.process/start` for starting the process.

(def ls (p/start "ls"))


; ## Reading its output
;
; To read from this `ls`, we first get its standard output.

(def ls-stdout (p/stdout ls))

; Note that it is an `InputStream`, because we can read from it. From *our*
; perspective, it's an input.

; Let's read:

(slurp ls-stdout)

; # A step up with `cat`
;
; Our next case is `cat`, which takes input *and* output.

; First, we start the process again.

(def cat (p/start "cat"))

; ## Writing to it

; To write to the process we need its standard input.

(def cat-stdin (p/stdin cat))

; Note that this is an `OutputStream`, because from *our* perspective, it is an
; output (we can write to it).
;
; In clojure, to write to a stream, we can rebind `*out*` (normally standard
; output) locally, so the output is written to the process instead.
; We write three lines to `cat`

(binding [*out* (io/writer cat-stdin)]
  (println "hi")
  (println "there.")
  (println "Clojure is nice!"))

; ## Reading from it

; To start reading, we first close `cat`'s standard input. We do this because
; `slurp` (used later) waits for the full output.

(.close cat-stdin)

; Now we read it:

(slurp (p/stdout cat))

