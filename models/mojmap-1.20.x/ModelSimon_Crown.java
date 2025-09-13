// Made with Blockbench 4.10.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelSimon_Crown<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "simon_crown"), "main");
	private final ModelPart crown;

	public ModelSimon_Crown(ModelPart root) {
		this.crown = root.getChild("crown");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition crown = partdefinition.addOrReplaceChild("crown",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-4.0F, -9.75F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(0, 5)
						.addBox(-0.5F, -10.25F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 4)
						.addBox(-0.5F, -11.25F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 1)
						.addBox(0.0F, -11.25F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-1.0F, -11.25F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 2)
						.addBox(-0.5F, -11.75F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(4, 11)
						.addBox(-4.35F, -7.0F, -1.0F, 0.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(10, 9)
						.addBox(-4.85F, -3.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(0, 9)
						.addBox(-4.85F, -1.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(0, 22)
						.addBox(-4.85F, -2.0F, 0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(20, 17)
						.addBox(-4.85F, -2.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(3.85F, -3.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(0, 20)
						.addBox(3.85F, -2.0F, 0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 4)
						.addBox(3.85F, -1.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(4, 20)
						.addBox(3.85F, -2.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 11)
						.addBox(4.35F, -7.0F, -1.0F, 0.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = crown.addOrReplaceChild("cube_r1",
				CubeListBuilder.create().texOffs(13, 14).addBox(-1.0F, -3.0F, -1.0F, 1.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-3.5F, -7.0F, 0.5F, 0.0F, 0.0F, -0.1309F));

		PartDefinition cube_r2 = crown.addOrReplaceChild("cube_r2",
				CubeListBuilder.create().texOffs(0, 9).addBox(-1.0F, -3.0F, -4.0F, 1.0F, 3.0F, 8.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-3.5F, -6.5F, 0.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition cube_r3 = crown.addOrReplaceChild("cube_r3",
				CubeListBuilder.create().texOffs(15, 9).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.5F, -7.0F, 3.5F, -0.1309F, 0.0F, 0.0F));

		PartDefinition cube_r4 = crown.addOrReplaceChild("cube_r4",
				CubeListBuilder.create().texOffs(20, 9).addBox(-4.0F, -3.0F, 0.0F, 8.0F, 3.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -6.5F, 3.5F, -0.1309F, 0.0F, 0.0F));

		PartDefinition cube_r5 = crown.addOrReplaceChild("cube_r5",
				CubeListBuilder.create().texOffs(10, 13).addBox(0.0F, -3.0F, -1.0F, 1.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.5F, -7.5F, -3.6F, 0.1309F, 0.0F, 0.0F));

		PartDefinition cube_r6 = crown.addOrReplaceChild("cube_r6",
				CubeListBuilder.create().texOffs(20, 13).addBox(-4.0F, -3.0F, -1.0F, 8.0F, 3.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -6.5F, -3.5F, 0.1309F, 0.0F, 0.0F));

		PartDefinition cube_r7 = crown.addOrReplaceChild("cube_r7",
				CubeListBuilder.create().texOffs(10, 15).addBox(0.0F, -3.0F, 1.0F, 1.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(3.5F, -7.0F, -1.5F, 0.0F, 0.0F, 0.1309F));

		PartDefinition cube_r8 = crown.addOrReplaceChild("cube_r8",
				CubeListBuilder.create().texOffs(10, 12).addBox(0.0F, -3.0F, -3.0F, 1.0F, 3.0F, 8.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(3.5F, -6.5F, -1.0F, 0.0F, 0.0F, 0.1309F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		crown.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}