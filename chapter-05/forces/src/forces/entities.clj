(ns forces.entities
  (:require [forces.ces :as ces]
            [forces.renderers :as r]
            [org.nfrac.cljbox2d.core :as box]
            [org.nfrac.cljbox2d.vec2d :as v2]))

(def not-nil? (complement nil?))

(defn world [world]
  (ces/entity {:world world}))

(defn camera 
  ([width height center]
    (camera {:width width :height height :center center}))
  ([data]
    (ces/entity {:camera data})))

(defn ground [world]
  (ces/entity {:ground nil
               :body (box/body! world {:type :static})}))

(defn astro-body [world pos density]
  (let [id (ces/id)
        restitution 0.7
        radius-factor 4
        radius (* density radius-factor)
        body (box/body! world {:position pos :user-data id} 
                              {:shape (box/circle radius) :restitution restitution :density density})
        hit-test (fn [v] 
                   (let [fixture (box/fixture-of body)
                         inside (.testPoint fixture (box/vec2 v))]
                     (if (true? inside) body nil)))
        grow-func (fn [components op] 
                   (if (true? (get-in components [:selectable :selected]))
                     (let [fixture (box/fixture-of body)
                           new-density (op (.getDensity fixture) 0.03)
                           new-radius (* new-density radius-factor)]
                       (.destroyFixture body fixture)
                       (box/fixture! body {:shape (box/circle new-radius) :restitution restitution :density new-density})
                       ;; (.resetMassData body) ;; - not sure if this is required after removing and adding fixtures, doesn't look like it!
                       (assoc-in components [:radius] new-radius))
                     components))]
    (ces/entity id {:astro-body nil
                    :renderable r/astro-body
                    :radius radius
                    :body body
                    :collideable {:collided false}
                    :gravity {:get-mass #(box/mass body)}
                    :selectable {:selected false 
                                 :hit-test hit-test}
                    :draggable {:hit-test hit-test}
                    ;; Because we want to limit the logic contained within entities (logic should be mainly in systems),
                    ;; perhaps it might be better to move the control functions in a separate system..
                    ;; Eg, we could have a system that controls astro bodies (:controllable :astro-body) and a system
                    ;; that controls cones (:controllable :cone) etc..                 
                    :controllable {:controls {:up #(-> % (grow-func +))
                                              :down #(-> % (grow-func -))}}})))

(defn car [world]
  (let [chasis {:width 4 :height 2 :position [10 20]}
        back-wheel {:radius 0.5 :position [8.5 19]}
        front-wheel {:radius 0.5 :position [11.5 19]}
        chasis-body (box/body! world {:position (:position chasis) :type :dynamic} {:shape (box/box (/ (:width chasis) 2) (/ (:height chasis) 2)) :restitution 0.01})
        back-wheel-body (box/body! world {:position (:position back-wheel)} {:shape (box/circle (:radius back-wheel)) :restitution 0.01 :friction 1})
        front-wheel-body (box/body! world {:position (:position front-wheel)} {:shape (box/circle (:radius front-wheel)) :restitution 0.01 :friction 1})
        back-wheel-joint (box/joint! {:type :revolute
                                      :body-a chasis-body
                                      :body-b back-wheel-body
                                      :world-anchor (:position back-wheel)
                                      :enable-motor true
                                      :motor-speed (* (- v2/PI) 10)
                                      :max-motor-torque 100000})
        front-wheel-joint (box/joint! {:type :revolute
                                       :body-a chasis-body
                                       :body-b front-wheel-body
                                       :world-anchor (:position front-wheel)})]
    (ces/entity {:car nil
                 :renderable r/car
                 :draggable {:hit-test (fn [v] 
                                         (first 
                                           (filter not-nil? 
                                                   (map (fn [body]
                                                      (let [fixture (box/fixture-of body)
                                                            inside (.testPoint fixture (box/vec2 v))]
                                                        (if (true? inside)
                                                          body
                                                          nil)))
                                                      [back-wheel-body front-wheel-body chasis-body]))))}
                 :chasis chasis
                 :back-wheel back-wheel
                 :front-wheel front-wheel
                 :chasis-body chasis-body
                 :back-wheel-body back-wheel-body
                 :front-wheel-body front-wheel-body
                 :back-wheel-joint back-wheel-joint
                 :front-wheel-joint front-wheel-joint})))

(defn windmill [world]
  (let [sail {:width 10 :height 1 :position [20 9.5]}
        base {:width 1 :height 10 :position [20 5]}
        sail-body (box/body! world {:position (:position sail) :type :dynamic} {:shape (box/box (/ (:width sail) 2) (/ (:height sail) 2)) :restitution 0.7})
        base-body (box/body! world {:position (:position base) :type :static} {:shape (box/box (/ (:width base) 2) (/ (:height base) 2)) :restitution 0.7})
        joint (box/joint! {:type :revolute
                           :body-a base-body
                           :body-b sail-body
                           :world-anchor [20 9.5]
                           :enable-motor true
                           :motor-speed (- v2/PI)
                           :max-motor-torque 10000})]
     (ces/entity {:windmill nil
                  :renderable r/windmill
                  :sail sail
                  :base base
                  :sail-body sail-body
                  :base-body base-body
                  :joint joint})))

(defn cone [world pos]
  (let [radius 0.5
        vertices [[0 -2] [0.5 0] [-0.5 0]]
        body (box/body! world {:position pos} 
                              {:shape (box/circle radius) :restitution 0.7}
                              {:shape (box/polygon vertices) :restitution 0.7})]
    (ces/entity {:cone nil
                 :renderable r/cone
                 :radius radius
                 :vertices vertices
                 :body body
                 :draggable {:hit-test (fn [v] 
                                         (first 
                                           (filter not-nil? 
                                                   (map (fn [fixture]
                                                      (let [inside (.testPoint fixture (box/vec2 v))]
                                                        (if (true? inside)
                                                          body
                                                          nil)))
                                                      (box/fixtureseq body)))))}})))
                                                        
(defn flower [world pos]
  (let [petal-color [154 229 125]
        bud-color [232 177 91]
        petals [{:position [0.5 0.5] :radius 0.7 :color petal-color}
                {:position [-0.5 0.5] :radius 0.7 :color petal-color}
                {:position [-0.5 -0.5] :radius 0.7 :color petal-color}
                {:position [0.5 -0.5] :radius 0.7 :color petal-color}
                {:position [0 0] :radius 0.5 :color bud-color}]
        body (apply box/body! world {:position pos} (map (fn [x] {:shape (box/circle (:radius x) (:position x)) :restitution 0.7}) petals))]
    (ces/entity {:flower nil
                 :renderable r/flower
                 :petals petals
                 :body body
                 :draggable {:hit-test (fn [v] 
                                         (first 
                                           (filter not-nil? 
                                                   (map (fn [fixture]
                                                      (let [inside (.testPoint fixture (box/vec2 v))]
                                                        (if (true? inside)
                                                          body
                                                          nil)))
                                                      (box/fixtureseq body)))))}})))

(defn surface [world vertices]
  (ces/entity {:chain nil
           :renderable r/surface
           :vertices vertices
           :body (box/body! world {:type :static} {:shape (box/edge-chain vertices) :restitution 0.7})}))

