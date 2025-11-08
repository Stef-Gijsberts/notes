; In this document I explore a bit about how to use the new tools for
; subprocesses introduced in clojure 1.12.

; Let's first include it:
(ns clojure-subprocesses
  (:require
   [clojure.java.io :as io]
   [clojure.java.process :as p]
   [nextjournal.clerk :as clerk]))

(comment
  (nextjournal.clerk/serve! {:watch-paths ["notes"]}))

; What's in the namespace?

(clerk/html
 [:table
  [:tbody
   (for [[sym var] (ns-publics 'clojure.java.process)]
     (let [m (meta var)]
       [:tr
        [:td [:tt sym (str (:arglists m))]]
        [:td [:pre (:doc m)]]]))]])

; Let's try to start a process. I'll start `ls`, because it's simple and
; doesn't require any inputs.
(def ls (p/start "ls"))

; We've got a process. What now? We get the output.

(def ls-stdout (p/stdout ls))

; And read from the stream:

(def ls-output (slurp ls-stdout))

; Okay, now let's try another process, `cat`, to know how to write.

(def cat (p/start "cat"))

; Write to it:
(def cat-stdin (p/stdin cat))

(binding [*out* (io/writer cat-stdin)]
  (println "hi")
  (println "there.")
  (println "Clojure is nice!"))

(.close cat-stdin)

; Read from it:

(slurp (p/stdout cat))

