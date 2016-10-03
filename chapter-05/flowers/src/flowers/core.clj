(ns flowers.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-ces.core :refer :all]
            [flowers.systems :refer :all]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :refer :all]
            [org.nfrac.cljbox2d.vec2d :refer :all]))

(def fps 60)

(defn render-chain [camera chain-components]
  (q/no-fill)
  (q/begin-shape)
    (doseq [vertex (:vertices chain-components)]
      (let [[x y] (tb/world-to-px camera vertex)]
        (q/curve-vertex x y)))
  (q/end-shape))

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
  (let [vertices [[0 -2] [0.5 0] [-0.5 0]]]
    (entity {:label :cone
             :renderable render-cone
             :radius 0.5
             :vertices vertices
             :body (body! world {:position pos} 
                          {:shape (circle 0.5) :restitution 0.7}
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
  (let [petals [{:position [0 1] :radius 0.7 :color [50 50 50]}
                {:position [1 0] :radius 0.7 :color [50 50 50]}
                {:position [-1 0] :radius 0.7 :color [50 50 50]}
                {:position [0 0] :radius 0.5 :color [255 255 255]} ;; Flower Bud
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
           (entity {:label :chain
                    :renderable render-chain
                    :vertices vertices
                    :body (body! world {:type :static} {:shape (edge-chain vertices) :restitution 0.7})}))))

(defn prog-loop [entities]
  (q/background 240)
  (-> entities
      (ticker (/ 1 fps))
      (click-and-spawn create-flower :left)
      (click-and-spawn create-cone :right)
      (renderer)))

(defn -main []
  (q/defsketch flowers
    :title "Flowers"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
