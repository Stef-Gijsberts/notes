; Try this on your own computer!
;
; It uses `aplay`, a simple command line tool that simply takes in audio samples
; and plays them back.
;
; ```clj
; (ns sinewave
;   (:require
;    [clojure.java.process :as p]
;    [clojure.math :refer [PI sin]]))
; 
; (def sample-rate-hz 44000)
; 
; (def aplay (p/start "aplay" "-r" (str sample-rate-hz)))
; 
; (def aplay-stdin (p/stdin aplay))
; 
; (doseq [t (range 0 sample-rate-hz)]
;   (.write aplay-stdin
;           (byte (* 127 (sin (* 2 PI 440 (/ t sample-rate-hz)))))))
; ```