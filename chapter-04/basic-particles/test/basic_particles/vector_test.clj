(ns basic-particles.vector-test
  (:require [clojure.test :refer :all]
            [basic-particles.protocols.vector-operations :refer :all]
            [basic-particles.vector])
  (:import [basic_particles.vector Vector]))

(deftest multiply-vector
  (testing "Multiply a vector"
    (is (= (multiply (Vector. 2 2) 4) (Vector. 8 8)))))

(deftest divide-vector
  (testing "Divide a vector"
    (is (= (divide (Vector. 2 2) 4) (Vector. 1/2 1/2)))))

(deftest normalize-vector
  (testing "Get the magnitude of a vector"
    (is (= (normalize (Vector. 3 4)) (Vector. 0.6 0.8)))))

(deftest get-magnitude
  (testing "Get the magnitude of a vector"
    (is (= (magnitude (Vector. 3 4)) 5.0))))

(deftest add-vectors
  (testing "Adding two vectors"
    (is (= (add (Vector. 1 2) (Vector. 2 3)) (Vector. 3 5)))))

(deftest add-many-vectors
  (testing "Adding many vectors"
    (is (= (-> (Vector. 1 1)
               (add (Vector. 2 2))
               (add (Vector. 3 3))) 
           (Vector. 6 6)))))

(deftest add-many-vectors-reduce
  (testing "Adding many vectors with reduce"
    (is (= (reduce add [(Vector. 1 1) (Vector. 2 2) (Vector. 3 3)])
           (Vector. 6 6)))))

(deftest subtract-vectors
  (testing "Subtracting two vectors"
    (is (= (subtract (Vector. 1 2) (Vector. 2 3)) (Vector. -1 -1)))))
