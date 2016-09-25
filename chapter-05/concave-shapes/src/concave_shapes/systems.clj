(ns concave-shapes.systems
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-ces.core :refer :all]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :refer :all]
            [org.nfrac.cljbox2d.vec2d :refer :all]))

(defn ticker [entities secs]
  (doseq [[id components] (filter-entities entities :world)]
    (let [world (:world components)]
      (step! world secs)))
  entities)

(defn left-click-and-spawn-ball [entities create-ball-func]
  (if (and (q/mouse-pressed?) (= (q/mouse-button) :left))
    (let [world (:world (second (first (filter-entities entities :world))))
          camera (:camera (second (first (filter-entities entities :camera))))
          mouse-pos [(q/mouse-x) (q/mouse-y)]
          world-pos (tb/px-to-world camera mouse-pos)]
      (merge entities (create-ball-func world world-pos)))
    entities))

(defn right-click-and-spawn-polygon [entities create-polygon-func]
  (if (and (q/mouse-pressed?) (= (q/mouse-button) :right))
    (let [world (:world (second (first (filter-entities entities :world))))
          camera (:camera (second (first (filter-entities entities :camera))))
          mouse-pos [(q/mouse-x) (q/mouse-y)]
          world-pos (tb/px-to-world camera mouse-pos)]
      (merge entities (create-polygon-func world world-pos)))
    entities))
  

(defn renderer [entities]
  (let [camera (:camera (second (first (filter-entities entities :camera))))]
    (doseq [[id components] (filter-entities entities :renderable)]
      (let [render-func (:renderable components)]
        (render-func camera components)))
    entities))