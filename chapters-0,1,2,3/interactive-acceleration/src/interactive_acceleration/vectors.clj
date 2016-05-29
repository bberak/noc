(ns interactive-acceleration.vectors
  (:require [quil.core :as q]))

(defn -vector-ops [op {x1 :x y1 :y} {x2 :x y2 :y}]
  {:x (op x1 x2)
   :y (op y1 y2)})

(defn add [v1 v2]
  (-vector-ops + v1 v2))

(defn subtract [v1 v2]
  (-vector-ops - v1 v2))

(defn multiply [{x :x y :y} n]
  {:x (* x n)
   :y (* y n)})

(defn divide [{x :x y :y} n]
  (if (= n 0.0)
    {:x x
     :y y}
	{:x (/ x n)
	 :y (/ y n)}))

(defn magnitude [{x :x y :y}]
  (q/sqrt (+ (q/pow x 2) (q/pow y 2))))

(defn normalize [v]
  (let [mag (magnitude v)]
    (divide v mag)))

(defn limit [v n]
  (let [mag (magnitude v)]
    (if (> mag n)
      (multiply (normalize v) n)
      v)))

(defn random []
  (let [;;v {:x (q/random -100 100) :y (q/random -100 100)}
        v {:x (q/random-gaussian) :y (q/random-gaussian)}]
    (normalize v)))