(ns basic-particles.vectors
  (:require [quil.core :as q]))

(defprotocol IVectorOperations
  (multiply [v n])
  (divide [v n])
  (add [v1 v2])
  (subtract [v1 v2])
  (magnitude [v])
  (normalize [v]))

(defrecord Vector [x y]
  IVectorOperations
  (multiply [v n]
  	(Vector. (* x n) (* y n)))
  (divide [v n]
  	(Vector. (/ x n) (/ y n)))
  (add [v1 v2]
  	(Vector. (+ x (:x v2)) (+ y (:y v2))))
  (subtract [v1 v2]
  	(Vector. (- x (:x v2)) (- y (:y v2))))
  (magnitude [v]
  	(q/sqrt (+ (q/sq x) (q/sq y))))
  (normalize [v]
  	(let [mag (magnitude v)]
     (divide v mag))))