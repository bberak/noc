(ns car.systems
  (:require [quil.core :as q]
            [basic-ces.core :as ces]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :as box]))

(defn tick [entities secs]
  (doseq [[id components] (ces/filter-entities entities :world)]
    (let [world (:world components)]
      (box/step! world secs)))
  entities)

(defn click-and-spawn [entities entity-func button]
  (if (and (q/mouse-pressed?) (= (q/mouse-button) button))
    (let [world (:world (second (first (ces/filter-entities entities :world))))
          camera (:camera (second (first (ces/filter-entities entities :camera))))
          mouse-pos [(q/mouse-x) (q/mouse-y)]
          world-pos (tb/px-to-world camera mouse-pos)]
      (merge entities (entity-func world world-pos)))
    entities))

(defn renderer [entities]
  (let [camera (:camera (second (first (ces/filter-entities entities :camera))))]
    (doseq [[id components] (ces/filter-entities entities :renderable)]
      (let [render-func (:renderable components)]
        (render-func camera components)))
    entities))