(ns basic-particles.records.vector2d-test
  (:require [clojure.test :refer :all]
            [basic-particles.protocols.vector :refer :all]
            [basic-particles.records.vector2d])
  (:import [basic_particles.records.vector2d Vector2D]))

(deftest multiply-vector
  (testing "Multiply a vector"
    (is (= (multiply (Vector2D. 2 2) 4) (Vector2D. 8 8)))))

(deftest divide-vector
  (testing "Divide a vector"
    (is (= (divide (Vector2D. 2 2) 4) (Vector2D. 1/2 1/2)))))

(deftest normalize-vector
  (testing "Get the magnitude of a vector"
    (is (= (normalize (Vector2D. 3 4)) (Vector2D. 0.6 0.8)))))

(deftest get-magnitude
  (testing "Get the magnitude of a vector"
    (is (= (magnitude (Vector2D. 3 4)) 5.0))))

(deftest add-vectors
  (testing "Adding two vectors"
    (is (= (add (Vector2D. 1 2) (Vector2D. 2 3)) (Vector2D. 3 5)))))

(deftest add-many-vectors
  (testing "Adding many vectors"
    (is (= (-> (Vector2D. 1 1)
               (add (Vector2D. 2 2))
               (add (Vector2D. 3 3))) 
           (Vector2D. 6 6)))))

(deftest add-many-vectors-reduce
  (testing "Adding many vectors with reduce"
    (is (= (reduce add [(Vector2D. 1 1) (Vector2D. 2 2) (Vector2D. 3 3)])
           (Vector2D. 6 6)))))

(deftest subtract-vectors
  (testing "Subtracting two vectors"
    (is (= (subtract (Vector2D. 1 2) (Vector2D. 2 3)) (Vector2D. -1 -1)))))
