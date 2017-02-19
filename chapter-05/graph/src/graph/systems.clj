(ns graph.systems
  (:require [graph.helpers :refer :all]
            [graph.ces :as ces]
            [quil.core :as q]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :as box]
            [org.nfrac.cljbox2d.vec2d :as v2]))

(defn game-time 
  ([entities]
    (doseq [[id components] (ces/filter-entities entities :physics)]
      (let [physics (:physics components)]
        (.update physics)))
    entities)
  ([entities secs]
    (doseq [[id components] (ces/filter-entities entities :world)]
      (let [world (:world components)]
        (box/step! world secs)))
    entities))

(defn control [entities]
  (reduce 
    (fn [agg [id components]]
      (let [controls (get-in components [:controllable :controls])
            current-key (q/key-as-keyword)
            key-func (current-key controls)]
        (if (and (q/key-pressed?) (not-nil? key-func))
          (assoc-in agg [id] (key-func components))
          agg)))
    entities
    (ces/filter-entities entities :controllable)))

(defn selection [entities]
  (if (q/mouse-pressed?)
    (let [camera (:camera (second (first (ces/filter-entities entities :camera))))
          mouse-px-pos [(q/mouse-x) (q/mouse-y)]
          mouse-world-pos (tb/px-to-world camera mouse-px-pos)]
      (reduce 
        (fn [agg [id components]]
          (let [hit-test (get-in components [:selectable :hit-test])
                clicked (not-nil? (hit-test mouse-world-pos))]
            (assoc-in agg [id :selectable :selected] clicked)))
        entities
        (ces/filter-entities entities :selectable)))
    entities))

;; This should be abstracted so the system does not know about the underlying
;; physics system (Box2D, Verlet, custom). I should use functions like get-pos,
;; get-mass and apply-force on the entities with primitive data types as arguments.
;; To support multi-body entities I can use a get-bodies function.
(defn gravity [entities]
  (let [gravity-bodies (ces/filter-entities entities :gravity)
        gravity-func (fn [components-1 components-2]
                       (let [gravitational-constant 8
                             body-1 (get-in components-1 [:body])
                             mass-1 ((get-in components-1 [:gravity :get-mass]))
                             pos-1 (box/position body-1)
                             body-2 (get-in components-2 [:body])
                             mass-2 ((get-in components-2 [:gravity :get-mass]))
                             pos-2 (box/position body-2)
                             dir (v2/v-sub pos-2 pos-1)
                             dist (v2/v-dist pos-1 pos-2)
                             r-unit (v2/v-scale dir)
                             r-squared (* dist dist)]
                         (v2/v-scale r-unit (/ (* gravitational-constant mass-1 mass-2) r-squared))))]
        (doseq [[id components] gravity-bodies]
          (let [body (get-in components [:body])
                pos (box/position body)
                others (dissoc gravity-bodies id)
                forces (map (fn [[other-id other-components]] (gravity-func components other-components)) others)
                agg-force (reduce v2/v-add [0 0] forces)]
            (box/apply-force! body agg-force pos)))
        entities))

;; Because I can track the previous 'collided' state and the current 'collided' state of an entity,
;; it makes it fairly simple to add 'beginCollision', 'endCollision', and 'onCollision|stillColliding' event handlers (callbacks)
;; on the entity's :collideable components..
(defn collision [entities]
  (let [collideables (ces/filter-entities entities :collideable)]
    (if (not-empty collideables)
      (let [world (:world (second (first (ces/filter-entities entities :world))))
            contacts (box/all-current-contacts world)
            colliding-entity-ids (flatten 
                                   [(map (fn [contact]
                                          (let [body-a (box/body-of (:fixture-a contact))
                                                body-b (box/body-of (:fixture-b contact))]
                                            (filter not-nil? [(box/user-data body-a) (box/user-data body-b)]))) contacts)])]
        (reduce (fn [agg [id components]]
                  (let [collided (not-nil? (some #{id} colliding-entity-ids))]
                    (assoc-in agg [id :collideable :collided] collided)))
                entities 
                collideables))
      entities)))


(defn spawning [entities entity-func button]
  (if (and (q/mouse-pressed?) (= (q/mouse-button) button))
    (let [world (:world (second (first (ces/filter-entities entities :world))))
          camera (:camera (second (first (ces/filter-entities entities :camera))))
          mouse-px-pos [(q/mouse-x) (q/mouse-y)]
          mouse-world-pos (tb/px-to-world camera mouse-px-pos)
          draggables (ces/filter-entities entities :draggable)
          dragging (any? not-nil? (map (fn [[id components]] (get-in components [:draggable :joint])) draggables))
          selectables (ces/filter-entities entities :selectable)
          selecting (any? true? (map (fn [[id components]] (get-in components [:selectable :selected])) selectables))]
      (if (and (false? dragging) (false? selecting)) ;; Only spawn new entities if nothing is being dragged or selected
        (merge entities (entity-func world mouse-world-pos))
        entities))
    entities))

(defn box2d-rendering [entities]
  (let [camera (:camera (second (first (ces/filter-entities entities :camera))))]
    (doseq [[id components] (ces/filter-entities entities :renderable)]
      (let [render-func (:renderable components)]
        (render-func camera components)))
    entities))

(defn rendering [entities]
  (q/background 255)
  (doseq [[id components] (ces/filter-entities entities :renderable)]
    (let [render-func (:renderable components)]
      (render-func components)))
  entities)

(defn box2d-dragging [entities button]
  (let [world (:world (second (first (ces/filter-entities entities :world))))
        ground-body (first (filter #(= :static (box/body-type %)) (box/bodyseq world)))
        camera (:camera (second (first (ces/filter-entities entities :camera))))
        mouse-down (and (q/mouse-pressed?) (= (q/mouse-button) button))
        draggables (ces/filter-entities entities :draggable)
        dragging (any? not-nil? (map (fn [[id components]] (get-in components [:draggable :joint])) draggables))]
    (if (true? mouse-down)    
      ;; Mouse is down, find the components that are being clicked, update-or-create their joints
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

(def previous-mouse-position (atom nil))
(def previous-mouse-button (atom nil))
(def mouse-position (atom nil))
(def mouse-button (atom nil))
(def previous-mouse-dragged? (atom false))
(def mouse-dragged? (atom false))
(def mouse-clicked? (atom false))

(defn mouse-input [entities]
  (reset! previous-mouse-position @mouse-position)
  (reset! previous-mouse-button @mouse-button)
  (reset! mouse-position [(q/mouse-x) (q/mouse-y)])
  (reset! mouse-button (cond (q/mouse-pressed?) (q/mouse-button) :else nil))
  (reset! previous-mouse-dragged? @mouse-dragged?)
  (reset! mouse-dragged? (and 
                           (not-nil? @mouse-button)
                           (= @previous-mouse-button @mouse-button)
                           (not-nil? @previous-mouse-position)
                           (not-nil? @mouse-position)
                           (or (not= @previous-mouse-position @mouse-position) (true? @previous-mouse-dragged?))))
  (reset! mouse-clicked? (and
                           (not-nil? @previous-mouse-button)
                           (nil? @mouse-button)
                           (false? @previous-mouse-dragged?)))
  entities)

(defn dragging [entities]
  (cond 

    @mouse-dragged?
    (reduce 
      (fn [agg [id components]]
        (let [on-drag (get-in components [:draggable :on-drag])]
          (assoc-in agg [id] (on-drag @mouse-button @previous-mouse-position @mouse-position components))))
      entities
      (ces/filter-entities entities :draggable))

    (and (true? @previous-mouse-dragged?) (false? @mouse-dragged?))
    (reduce 
      (fn [agg [id components]]
        (let [on-drag-end (get-in components [:draggable :on-drag-end])]
          (assoc-in agg [id] (on-drag-end components))))
      entities
      (ces/filter-entities entities :draggable))

    :else
    entities))

(defn clicking [entities]
  (if @mouse-clicked?
    (reduce
      (fn [agg [id components]]
        (let [on-click (get-in components [:clickable :on-click])]
          (assoc-in agg [id] (on-click @mouse-button @mouse-position components))))
      entities
      (ces/filter-entities entities :clickable))
    entities))