(ns advanced-particles.records.vector2d
  (:require [quil.core :as q]
            [advanced-particles.protocols.vector :refer :all]))

(defrecord Vector2D [x y]
  Vector
  (multiply [v n]
  	(Vector2D. (* x n) (* y n)))
  (divide [v n]
  	(Vector2D. (/ x n) (/ y n)))
  (add [v1 v2]
  	(Vector2D. (+ x (:x v2)) (+ y (:y v2))))
  (subtract [v1 v2]
  	(Vector2D. (- x (:x v2)) (- y (:y v2))))
  (magnitude [v]
  	(q/sqrt (+ (q/sq x) (q/sq y))))
  (normalize [v]
  	(let [mag (magnitude v)]
     (divide v mag))))