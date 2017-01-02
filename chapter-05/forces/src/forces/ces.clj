(ns forces.ces)

(defn filter-entities [entities & components]
  (merge {} (filter (fn [[k v]] (every? #(contains? v %) components)) entities)))

(defn id []
  (keyword (str (java.util.UUID/randomUUID))))

(defn entity 
  ([components] (entity (id) components))
  ([id components]
  	{id components}))

