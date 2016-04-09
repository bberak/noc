(ns waves2.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [waves2.vectors :as v]
            [waves2.forces :as f]
            [waves2.waves :as w]))

(defn setup []
  (q/frame-rate 60)
  {:waves [(assoc (w/sine-wave (v/create 50 100) 400 150 15 1.95 25 0) :render w/render)
           (assoc (w/cosine-wave (v/create 100 450) 400 250 30 0.95 20 -0.5) :render #(w/render-ellipses % 25 25 50 50 50 120))
           (assoc (w/cosine-wave (v/create 350 400) 400 250 30 0.95 5 -0.25) :render #(w/render-rects % 50 50 50 50 50 120))
           (assoc (w/cosine-wave (v/create 400 100) 400 250 45 0.95 20 0) :render #(w/render-rects-with-heading % 40 40 50 50 50 120))        
           ;; Moving wave that created with several sine and cosine functions
           (assoc 
             (w/wave (v/create 10 500) 400 150 15 0.95 45 1 (fn [amp theta period]
                                                                (+ 
                                                                  (* (* amp (q/sin (* w/two-pi (/ theta period)))) 0.65)
                                                                  (+ (* amp (q/cos (* w/two-pi (/ theta period)))) (- 22 period))
                                                                  (- (* amp (q/sin (* w/two-pi (/ theta period)))) (* theta 2))
                                                                  (/ (* amp (q/cos (* w/two-pi (/ theta period)))) amp)
                                                                  (* amp (q/sin (* w/two-pi (/ theta period)))))))
             :render #(w/render-ellipses % 50 50 50 50 50 120))   
           ;; Organic wave created using perlin noise
           (assoc 
             (w/wave (v/create 60 200) 400 150 15 0.95 45 0 (fn [amp theta period]
                                                              (q/noise-detail 1)
                                                              (let [noise (q/noise (* theta 0.01))]
                                                                  (* (* amp noise 8) (q/sin (* w/two-pi (/ theta period)))                       
                                                                ))))
             :render #(w/render-ellipses % 50 50 50 50 50 120))
           
           
           ]})

(defn update-state [{waves :waves}]
  {:waves (map (fn[w] (w/update-wave w)) waves)})

(defn draw-state [{waves :waves}]
  (q/background 255)
  (doseq [w waves]
    (let [render-func (:render w)]
      (render-func w))))

(defn -main []
  (q/defsketch waves2
    :title "Waves 2"
    :size [840 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
