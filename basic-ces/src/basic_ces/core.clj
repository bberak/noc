(ns basic-ces.core
  (:gen-class))

(defn filter-entities [state & components]
  (merge {} (filter (fn [[k v]] (every? #(contains? v %) components)) state)))

(defn map-entities [state func]
  (map (fn [[id components]]
         (let [result (func {id components})]
           {id result}))
       state))

(defn -entity-merge [input & coll]
  (reduce (fn [agg [id result]]
            (if (or (= result {}) (nil? result))
               (dissoc add id)
               (merge agg result)))
   
           input coll))

(defn entity [components]
  {(keyword (str (java.util.UUID/randomUUID))) components})

(defn system [state func]
  (apply -entity-merge state (func state)))

