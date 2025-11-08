(ns parser-combinators
  (:require
   [clojure.string :as str]))

; *Note*: this is not a finished post but rather some ideas scribbled down.
;
; ----------
;
; # Parser combinators
;
; A parser can be seen as a function, where its input is a sequence of symbols,
; and its output is what the sequence of symbols *means*, expressed as a value.
;
; ## Parsers as a map
;
; The simplest way to express a function is through a *map*.
;
; For learning purposes, we can express parsers as a map from strings to values.
;
; Parsers as a map can be fast for finite languages. In that case parsing is
; just a map lookup.
;
; But it will probably be slow for infinitely large languages (almost all
; languages found in practice are). Although it possible to describe them in
; Clojure using lazy infinite collections.
;
; For example the `(range)` function is an infinite collection of numbers. There
; is always a next number in the sequence.

(range)

; ### Our first parser as a map

; We create a parser for the ten digits:
; - `"0"` means `0`.
; - `"1"` means `1`.
; - .. and so forth

(def digit
  {"0" 0
   "1" 1
   "2" 2
   "3" 3
   "4" 4
   "5" 5
   "6" 6
   "7" 7
   "8" 8
   "9" 9})

; ### Parser combinators
;
; A parser combinator is a function that takes one or more parsers and creates a
; new parser.

; #### Concatenation
;
; A parser combinator for *concatenation* of two parsers: Given a parser `p1`
; and a parser `p2`, `(concat-2 p1 p2)` will be a new parser that first uses p1
; to parse and then p2.
;

(defn concat
  [& maps]
  (letfn [(step [maps]
            (if (empty? maps)
              (list [[] []])
              (for [[k v] (first maps)
                    [ks vs] (lazy-seq (step (rest maps)))]
                [(conj ks k) (conj vs v)])))]
    (map (fn [[ks vs]] [(apply str ks) vs])
         (step maps))))

(def two-digits
  (concat digit digit))

; #### Union
;
(defn union
  [& maps]
  (mapcat identity maps))

; An example using the `union` parser combinator.  Here, two parsers are
; combined. The `digit` parser, and the parser `{"a" :a}`. The result is a new
; parser.
(def digit-or-a
  (union digit {"a" :a}))

; #### Repetition

; A parser combinator for *repetition*. Given some parser `p`:
; - `(repeat 1 p)` = `p`, 
; - `(repeat 2 p)` = `(concat p p)`
; - `(repeat 3 p)` = `(concat p p p)`
; ...

(defn repeat [n p]
  (apply concat (clojure.core/repeat n p)))

(repeat 10 {"a" :a})

; #### Many

; Given some parser `p`, `(many p)` will
; be a new parser that parses the union of `(concat p)`, `(concat p p)`,
; `(concat p p p)`, and so forth.
;
; So effectively `(union (repeat 1 p) (repeat 2 p) (repeat 3 p) ...)`.

(defn many [p]
  (mapcat identity (for [n (range)]
                     (repeat n p))))

(defn digits->number [digits]
  (reduce (fn [acc d] (+ (* acc 10) d)) 0 digits))

(def number
  (for [[s r] (many digit)]
    [s (digits->number r)]))

(def binary-operator
  {"+" :+
   "-" :-
   "/" :/})

(def binary-expression
  (concat number binary-operator number))

; ## Parsers as a state machine
;
; Another way to represent parsers is as a state machine.

(def digit
  {:state/initial
   {\0 {:state :state/initial :result 0}
    \1 {:state :state/initial :result 1}}})

(defn state-machine->fn [state-machine]
  (let [t (fn [& {:keys [input state stack]}]
            ((state-machine state) (first input)))]))