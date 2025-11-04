export class activityDto {
    constructor(public name?: string,
        public type?: string,
        public description?:string,
        public roleId?:number,
        public status?: string,
        public processId?:number,
        public x?: number,
        public y?:number
    ){}
    
}