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
; (def sample-format :u8)
; 
; (def aplay (p/start "aplay" "-f" (name sample-format) "-r" (str sample-rate-hz)))
; 
; (def aplay-stdin (p/stdin aplay))
; 
; (defmulti sample->bytes (fn [sample-format _sample] sample-format))
; 
; (defmethod sample->bytes :u8 [_sample-format sample]
;   (byte-array [(-> sample
;               (/ 2.0)
;               (+ 0.5)
;               (* 128))]))
; 
; (doseq [t (range 0 sample-rate-hz)]
;   (.write aplay-stdin
;           (sample->bytes :u8 (sin (* 2 PI 440 (/ t sample-rate-hz))))))
; ```
