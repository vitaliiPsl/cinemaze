import React from "react";
import './Slider.css';

export default class Slider extends React.Component {
    constructor(props) {
        super(props);

        let slides = this.props.slides;
        let current = 0;

        this.state = {slides, current};
    }

    showPrevSlide(){
        let index = this.state.current - 1;
        if(index < 0){
            index = this.state.slides.length - 1;
        }

        this.setState({current: index});
    }

    showNextSlide(){
        let index = this.state.current + 1;

        if(index > this.state.slides.length - 1){
            index = 0;
        }

        this.setState({current: index});
    }

    setCurrent(index){
        this.setState({current: index});
    }

    render() {
        return (
            <div className="Slider">
                <div className="current-slide">
                    {this.state.slides[this.state.current]}
                    <a className="prev" onClick={() => this.showPrevSlide()}>❮</a>
                    <a className="next" onClick={() => this.showNextSlide()}>❯</a>
                </div>

                <div className="available-slides">
                    {this.state.slides.map((slide, index) =>
                        <div className={this.state.current === index ? "available-slide current" : "available-slide"} key={index} onClick={() => this.setCurrent(index)}>
                            {slide}
                        </div>
                    )
                    }
                </div>
            </div>
        );
    }
}