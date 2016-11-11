(ns car.systems
  (:require [quil.core :as q]
            [car.ces :as ces]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :as box]))

(def not-nil? (complement nil?))
(def any? (comp boolean some))

(defn tick [entities secs]
  (doseq [[id components] (ces/filter-entities entities :world)]
    (let [world (:world components)]
      (box/step! world secs)))
  entities)

(defn click-and-spawn [entities entity-func button]
  (if (and (q/mouse-pressed?) (= (q/mouse-button) button))
    (let [world (:world (second (first (ces/filter-entities entities :world))))
          camera (:camera (second (first (ces/filter-entities entities :camera))))
          mouse-px-pos [(q/mouse-x) (q/mouse-y)]
          mouse-world-pos (tb/px-to-world camera mouse-px-pos)
          draggables (ces/filter-entities entities :draggable)
          dragging (any? not-nil? (map (fn [[id components]] (get-in components [:draggable :joint])) draggables))]
      (if (false? dragging) ;; Only spawn new entities if nothing is being dragged
        (merge entities (entity-func world mouse-world-pos))
        entities))
    entities))

(defn renderer [entities]
  (let [camera (:camera (second (first (ces/filter-entities entities :camera))))]
    (doseq [[id components] (ces/filter-entities entities :renderable)]
      (let [render-func (:renderable components)]
        (render-func camera components)))
    entities))

(defn click-and-drag [entities button]
  (let [world (:world (second (first (ces/filter-entities entities :world))))
        ground-body (first (filter #(= :static (box/body-type %)) (box/bodyseq world)))
        camera (:camera (second (first (ces/filter-entities entities :camera))))
        mouse-down (and (q/mouse-pressed?) (= (q/mouse-button) button))
        draggables (ces/filter-entities entities :draggable)
        dragging (any? not-nil? (map (fn [[id components]] (get-in components [:draggable :joint])) draggables))]
    (if (true? mouse-down)
      
      ;; Mouse is down and noth, find the components that are being clicked, update-or-create their joints
      ;; Find the components that are not being clicked, destroy their joints
      (reduce 
        (fn [agg [id components]]
          (let [joint (get-in components [:draggable :joint])
                hit-test (get-in components [:draggable :hit-test])
                mouse-px-pos [(q/mouse-x) (q/mouse-y)]
                mouse-world-pos (tb/px-to-world camera mouse-px-pos)
                hit-test-body (hit-test mouse-world-pos)]
            (if (not-nil? joint)
              (let[]
                (.setTarget joint (box/vec2 mouse-world-pos))
                agg)
              (if (and (not-nil? hit-test-body) (false? dragging)) ;; Only drag a new body if there is nothing else being dragged
                (let [new-joint (box/joint! {:type :mouse
                                             :body-a ground-body
                                             :body-b hit-test-body
                                             :target mouse-world-pos
                                             :max-force 1000
                                             :frequency-hz 5 
                                             :damping-ratio 0.9})]
                  (assoc-in agg [id :draggable :joint] new-joint))
                agg))))
        entities
        draggables)
      
      ;; Mouse is no longer down, destroy and remove all mouse joints on the draggable components
      (reduce 
        (fn [agg [id components]]
          (let [joint (get-in components [:draggable :joint])]
            (if (not-nil? joint)
              (let []
                (.destroyJoint world joint)
                (assoc-in agg [id :draggable :joint] nil))
              agg)))
        entities
        draggables))))