(ns basic-ces.core
  (:gen-class))

(defn filter-entities [entities & components]
  (merge {} (filter (fn [[k v]] (every? #(contains? v %) components)) entities)))

(defn entity [components]
  {(keyword (str (java.util.UUID/randomUUID))) components})

