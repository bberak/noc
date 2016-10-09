(ns bridge.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-ces.core :refer :all]
            [bridge.systems :refer :all]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :refer :all]
            [org.nfrac.cljbox2d.vec2d :refer :all]))

(def fps 60)

(defn render-bridge [camera bridge-components]
  (q/fill 60)
  (let [radius (:radius bridge-components)
        scale (tb/world-to-px-scale camera)]
    (doseq [body (:bodies bridge-components)]
      (let [world-pos (position body)
            px-pos (tb/world-to-px camera world-pos)]
        (q/ellipse (first px-pos) (second px-pos) (* 2 radius scale) (* 2 radius scale))))))

(defn create-bridge [world]
  (let [radius 0.25
        restitution 0.7
        y 20
        left-post (body! world {:type :static :position [0 y] } {:shape (circle radius) :restitution restitution})
        right-post (body! world {:type :static :position [40 y] } {:shape (circle radius) :restitution restitution})
        mid-posts (map (fn [x] (body! world {:position [x 20] } {:shape (circle radius) :restitution restitution})) (range 1 40))
        bodies (concat [left-post] mid-posts [right-post])]
    (reduce 
      (fn [a b]
        (joint! {:type :distance
                 :length 1 
                 :frequency-hz 0
                 :damping-ratio 0
                 :body-a a 
                 :body-b b 
                 :anchor-a [0 0] 
                 :anchor-b [0 0] })
        b)
      bodies)
    (entity {:label :bridge
             :renderable render-bridge
             :radius radius
             :bodies bodies})))

(defn render-cone [camera cone-components]
  (let [body (:body cone-components)
        angle (* (angle body) -1)
        world-pos (position body)
        px-pos (tb/world-to-px camera world-pos)
        radius (:radius cone-components)
        scale (tb/world-to-px-scale camera)]
    (q/with-translation px-pos
      (q/with-rotation [angle]
        (q/fill 251 77 104)
        (q/ellipse 0 0 (* 2 radius scale) (* 2 radius scale))
        (q/fill 232 177 91)
        (q/begin-shape)
        (doseq [[x y] (map 
                           (fn [v] (v-scale v scale)) 
                           (poly-flip-y (:vertices cone-components)))]
          (q/vertex x y))
        (q/end-shape :close)))))

(defn create-cone [world pos]
  (let [radius 0.5
        vertices [[0 -2] [0.5 0] [-0.5 0]]]
    (entity {:label :cone
             :renderable render-cone
             :radius radius
             :vertices vertices
             :body (body! world {:position pos} 
                          {:shape (circle radius) :restitution 0.7}
                          {:shape (polygon vertices) :restitution 0.7})})))

(defn render-flower [camera flower-components]
  (let [body (:body flower-components)
        angle (* (angle body) -1)
        body-pos (position body)
        px-body-pos (tb/world-to-px camera body-pos)
        scale (tb/world-to-px-scale camera)]
    (q/with-translation px-body-pos
      (q/with-rotation [angle]
        (doseq [petal (:petals flower-components)]
          (let [petal-pos (first (poly-flip-y [(:position petal)]))
                px-petal-pos (v-scale petal-pos scale)
                radius (* (:radius petal) scale)
                color (:color petal)]
            (q/with-translation [px-petal-pos]
              (q/fill (nth color 0) (nth color 1) (nth color 2))
              (q/ellipse 0 0 (* 2 radius) (* 2 radius)))))))))

(defn create-flower [world pos]
  (let [petal-color [154 229 125]
        bud-color [232 177 91]
        petals [{:position [0.5 0.5] :radius 0.7 :color petal-color}
                {:position [-0.5 0.5] :radius 0.7 :color petal-color}
                {:position [-0.5 -0.5] :radius 0.7 :color petal-color}
                {:position [0.5 -0.5] :radius 0.7 :color petal-color}
                {:position [0 0] :radius 0.5 :color bud-color} ;; Flower Bud
                ]]
    (entity {:label :cone
             :renderable render-flower
             :petals petals
             :body (apply body! world {:position pos}
                          (map (fn [x] {:shape (circle (:radius x) (:position x)) :restitution 0.7}) petals))})))

(defn setup []
  (q/frame-rate fps)
  (q/noise-detail 2)
  (let [camera {:width 40
                :height 30
                :center [20 15]}
        world (new-world [0 -10])
        vertices (map (fn [x] [x (* (q/noise (/ x 5)) 20)]) (range 40))]
    (merge {}
           (entity {:label :camera
                    :camera camera}) 
           (entity {:label :world
                    :world world})
           (create-bridge world))))

(defn prog-loop [entities]
  (q/background 240)
  (-> entities
      (ticker (/ 1 fps))
      (click-and-spawn create-flower :left)
      (click-and-spawn create-cone :right)
      (renderer)))

(defn -main []
  (q/defsketch bridge
    :title "Bridge"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
