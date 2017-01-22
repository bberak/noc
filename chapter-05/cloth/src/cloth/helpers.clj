(ns cloth.helpers)

(def not-nil? (complement nil?))

(def any? (comp boolean some))

(defn log 
  ([anything] (println anything) anything)
  ([label anything] (println label anything) anything))