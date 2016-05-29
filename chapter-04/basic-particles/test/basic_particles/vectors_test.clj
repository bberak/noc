(ns basic-particles.vectors-test
  (:require [clojure.test :refer :all]
            [basic-particles.vectors :refer :all])
  (:import [basic_particles.vectors Vector]))

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
