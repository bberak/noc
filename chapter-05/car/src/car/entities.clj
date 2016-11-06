(ns car.entities
  (:require [basic-ces.core :as ces]
            [car.renderers :as r]
            [org.nfrac.cljbox2d.core :as box]
            [org.nfrac.cljbox2d.vec2d :as v2]))

(defn world [world]
  (ces/entity {:world world}))

(defn camera 
  ([width height center]
    (camera {:width width :height height :center center}))
  ([data]
    (ces/entity {:camera data})))

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
        vertices [[0 -2] [0.5 0] [-0.5 0]]]
    (ces/entity {:cone nil
             :renderable r/cone
             :radius radius
             :vertices vertices
             :body (box/body! world {:position pos} 
                          {:shape (box/circle radius) :restitution 0.7}
                          {:shape (box/polygon vertices) :restitution 0.7})})))

(defn flower [world pos]
  (let [petal-color [154 229 125]
        bud-color [232 177 91]
        petals [{:position [0.5 0.5] :radius 0.7 :color petal-color}
                {:position [-0.5 0.5] :radius 0.7 :color petal-color}
                {:position [-0.5 -0.5] :radius 0.7 :color petal-color}
                {:position [0.5 -0.5] :radius 0.7 :color petal-color}
                {:position [0 0] :radius 0.5 :color bud-color} ;; Flower Bud
                ]]
    (ces/entity {:cone nil
             :renderable r/flower
             :petals petals
             :body (apply box/body! world {:position pos}
                          (map (fn [x] {:shape (box/circle (:radius x) (:position x)) :restitution 0.7}) petals))})))
