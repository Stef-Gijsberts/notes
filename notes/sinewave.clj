; Try this on your own computer!
;
; ```clj
; (ns sinewave
;   (:require
;    [clojure.java.process :as p]
;    [clojure.math :refer [PI sin]]))
; 
; (def aplay (p/start "aplay"))
; 
; (def aplay-stdin (p/stdin aplay))
; 
; (doseq [t (range 0 10000)]
;   (.write aplay-stdin
;           (byte (* 127 (sin (* 2 PI 440 (/ t 8000)))))))
; ```