(ns basic-ces.core
  (:gen-class))

(defn filter-entities [entities & components]
  (merge {} (filter (fn [[k v]] (every? #(contains? v %) components)) entities)))

(defn map-entities [func entities]
  (map (fn [[id components]]
         (let [result (func id components)]
           {id result}))
       entities))

(defn -entity-merge [entities & coll]
  (reduce (fn [agg item]
            (let [[id result] (first item)]
            	(if (or (= result {}) (nil? result))
            		(dissoc agg id)
            		(merge agg result))))
           entities coll))

(defn entity [components]
  {(keyword (str (java.util.UUID/randomUUID))) components})

(defn system [entities func]
  (apply -entity-merge entities (func entities)))

