import React from "react";

export default class PreviewImage extends React.Component{
    constructor(props) {
        super(props);

        let image = '';
        this.state = {image};
    }

    componentDidMount() {
        let reader = new FileReader();
        reader.onload = (e) => this.setState({image: e.target.result});
        reader.readAsDataURL(this.props.image);
    }

    render() {
        return (
            <div className={this.props.className}>
                <img src={this.state.image} alt=""/>
                <div className="remove" onClick={this.props.remove}></div>
            </div>
        );
    }
}